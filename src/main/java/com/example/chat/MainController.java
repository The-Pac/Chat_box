package com.example.chat;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public String oAuth = "oauth:oig27sm2xk21wk9yg7nv84jk428ugl",
            client_id = "oauth:rkl7di51hjibc37otjo8wbhq6otxs2",
            chaine = "sha77etv";
    public int port = 6667;
    public Socket socket;
    public PrintWriter output;
    public BufferedReader bufferedReader;
    public boolean on_start = false;
    public ObservableList<VBox> tchat_msg_tab = FXCollections.observableArrayList();

    @FXML
    public HBox statut_Hbox;
    public VBox tchat_msg_Vbox;
    public Label statut_Label;
    public ScrollPane tchat_ScrollPane;
    public Button close_button;

    public void connect() {
        write("PASS " + oAuth);
        write("NICK " + chaine.toLowerCase());

    }

    public void join() {
        write("JOIN #" + chaine);
    }

    public void write(String msg) {
        this.output.println(msg);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //fxml components prep


        try {
            //create connexion
            socket = new Socket("irc.chat.twitch.tv", port);

            socket.setSoTimeout(1000);
            socket.setKeepAlive(true);

            output = new PrintWriter(socket.getOutputStream(), true);

            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //connect irc
            connect();

            Timeline timeline_connection = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
                try {
                    if (bufferedReader.ready()) {
                        String line = bufferedReader.readLine();

                        //join channel
                        if (line.contains("You are in a maze")) {
                            join();
                        }

                        System.out.println(line);
                        if (on_start && !line.contains("JOIN") && !line.contains("PING") && !line.contains("NICK") && !line.contains("PASS")) {
                            //init
                            Label pseudo = new Label(), msg = new Label(), date = new Label();
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                            VBox vBox = new VBox();
                            HBox hBox = new HBox();


                            //setstyle
                            vBox.setPrefWidth(370);
                            vBox.setMaxWidth(370);

                            vBox.setAlignment(Pos.CENTER);

                            hBox.setPrefWidth(360);
                            hBox.setMaxWidth(360);
                            hBox.setMaxHeight(5);

                            vBox.setPadding(new Insets(3, 5, 3, 5));
                            hBox.setSpacing(20);
                            pseudo.getStyleClass().add("msg_pseudo");
                            msg.getStyleClass().add("msg_msg");
                            date.getStyleClass().add("msg_msg");
                            vBox.getStyleClass().add("msg_vbox");

                            pseudo.maxWidth(hBox.getWidth() - 15);
                            msg.maxWidth(vBox.getWidth());
                            date.setMaxWidth(10);

                            pseudo.setWrapText(true);
                            msg.setWrapText(true);
                            date.setWrapText(true);


                            //split msg
                            String[] msg_split = line.substring(1).split(":");


                            //set text Label
                            msg.setText(msg_split[msg_split.length - 1]);
                            pseudo.setText(msg_split[0].split("!")[0] + ":");
                            date.setText(LocalDateTime.now().format(dateTimeFormatter));

                            //add
                            hBox.getChildren().addAll(pseudo, date);
                            vBox.getChildren().addAll(hBox, msg);
                            tchat_msg_Vbox.getChildren().add(vBox);

                        }
                        if (!on_start && line.split(":")[2].contains(">")) {
                            on_start = true;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
            timeline_connection.setCycleCount(Animation.INDEFINITE);
            timeline_connection.play();
            tchat_msg_Vbox.heightProperty().addListener((ChangeListener<? super Number>) (observable, oldvalue, newValue) -> tchat_ScrollPane.setVvalue((Double) newValue));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close_button_Action(ActionEvent actionEvent) {
        try {
            if (this.socket != null) {
                this.socket.close();
            }
            if (this.bufferedReader != null) {
                this.bufferedReader.close();
            }
            if (this.output != null) {
                this.output.close();
            }
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}