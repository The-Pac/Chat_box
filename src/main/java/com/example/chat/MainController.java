package com.example.chat;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public String oAuth = "oauth:oig27sm2xk21wk9yg7nv84jk428ugl",
            client_id = "oauth:rkl7di51hjibc37otjo8wbhq6otxs2",
            chaine = "pacreported";
    public int port = 6697;
    public Socket socket;
    public PrintWriter output;

    @FXML
    public HBox statut_Hbox;
    public Label statut_Label;
    public TextArea tchat_TextArea;

    public void connect(String password, String nickname) {
        write("PASS " + password);
        write("NICK " + nickname.toLowerCase());
    }

    public void write(String msg) {
        this.output.println(msg);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //fxml components prep
        tchat_TextArea.setEditable(false);


        try {
            socket = new Socket("irc.chat.twitch.tv", port);

            socket.setKeepAlive(true);
            socket.setSoTimeout(1000);

            output = new PrintWriter(socket.getOutputStream(), true);

            connect(oAuth, chaine);

            Timeline timeline_connection = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
                try {
                    if (socket.getInputStream().read() != -1) {
                        statut_Label.setText("connecté");
                        System.out.println("connecté");
                    } else {
                        statut_Label.setText("déconnecté");
                        System.out.println("déconnecté");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
            timeline_connection.setCycleCount(Animation.INDEFINITE);
            timeline_connection.play();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}