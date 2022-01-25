package com.example.chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
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
        Scene scene = new Scene(fxmlLoader.load(), 400, 230);
        stage.setX(10);
        stage.setY(0);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Chat Twitch");
        stage.getIcons().add(new Image("icon_app.png"));
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.show();
    }
}