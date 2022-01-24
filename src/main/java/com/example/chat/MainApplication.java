package com.example.chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainApplication extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("chat-view-FXML.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 200);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Chat Twitch");
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.show();
    }
}