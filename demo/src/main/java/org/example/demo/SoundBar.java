package org.example.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.MalformedURLException;

public class SoundBar extends Application {

    @Override
    public void start(Stage primaryStage) {
        Slider volumeSlider = new Slider(0, 1, 0.5);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(0.25);

        Slider timeSlider = new Slider();
        timeSlider.setMin(0);

        Slider startTimeSlider = new Slider();
        startTimeSlider.setMin(0);

        Slider endTimeSlider = new Slider();
        endTimeSlider.setMin(0);

        Label startTimeLabel = new Label("Start Time: 0:00");
        Label endTimeLabel = new Label("End Time: 0:00");

        Label timeLabel = new Label("Time: 0:00 / 0:00");

        Button playButton = new Button("Play");
        Button chooseFileButton = new Button("Выбрать трек");

        final MediaPlayer[] mediaPlayer = {null};

        chooseFileButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите аудиофайл");
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                try {
                    Media media = new Media(selectedFile.toURI().toURL().toString());
                    mediaPlayer[0] = new MediaPlayer(media);

                    mediaPlayer[0].currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                        timeSlider.setValue(newValue.toSeconds());
                        timeLabel.setText("Time: " + formatTime(newValue) + " / " + formatTime(mediaPlayer[0].getMedia().getDuration()));
                    });

                    mediaPlayer[0].setOnReady(() -> {
                        double duration = mediaPlayer[0].getMedia().getDuration().toSeconds();
                        timeSlider.setMax(duration);
                        startTimeSlider.setMax(duration);
                        endTimeSlider.setMax(duration);
                    });

                    mediaPlayer[0].setOnEndOfMedia(() -> {
                        mediaPlayer[0].stop();
                        playButton.setText("Play");
                    });

                    timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                        if (timeSlider.isValueChanging()) {
                            mediaPlayer[0].seek(Duration.seconds(newValue.doubleValue()));
                        }
                    });

                    mediaPlayer[0].play();
                    playButton.setText("Pause");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        playButton.setOnAction(event -> {
            if (mediaPlayer[0] != null) {
                if (mediaPlayer[0].getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer[0].pause();
                    playButton.setText("Play");
                } else {
                    mediaPlayer[0].play();
                    playButton.setText("Pause");
                }
            }
        });

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer[0] != null) {
                mediaPlayer[0].setVolume(newValue.doubleValue());
            }
        });

        startTimeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            startTimeLabel.setText("Start Time: " + formatTime(newValue));
            if (mediaPlayer[0] != null) {
                mediaPlayer[0].setStartTime(Duration.seconds(newValue.doubleValue()));
            }
        });
        endTimeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            endTimeLabel.setText("End Time: " + formatTime(newValue));
            if (mediaPlayer[0] != null) {
                mediaPlayer[0].setStopTime(Duration.seconds(newValue.doubleValue()));
            }
        });

        VBox root = new VBox(10, volumeSlider, timeSlider, startTimeSlider, endTimeSlider, startTimeLabel, endTimeLabel, timeLabel, playButton, chooseFileButton);
        root.setPrefSize(200, 300);
        root.setStyle("-fx-background-color: grey;");

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Audio Player");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private String formatTime(Duration duration) {
        int totalSeconds = (int) Math.floor(duration.toSeconds());
        int minutes = totalSeconds / 60;
        int remainderSeconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, remainderSeconds);
    }

    private String formatTime(Number seconds) {
        int totalSeconds = (int) Math.floor(seconds.doubleValue());
        int minutes = totalSeconds / 60;
        int remainderSeconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, remainderSeconds);
    }
}
