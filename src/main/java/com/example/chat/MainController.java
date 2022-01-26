package com.example.chat;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
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
            chaine = "pacreported", nickname = "pacreported";
    public int port = 6697;
    public Socket socket;
    public PrintWriter output;
    public BufferedReader bufferedReader;
    public boolean on_start = false, on_join = false, on_channel_show = false;

    @FXML
    public HBox statut_Hbox;
    public VBox tchat_msg_Vbox;
    public Label statut_Label;
    public ScrollPane tchat_ScrollPane;
    public Button close_button, channel_change_Button;
    public TextField channel_change_TextField;

    public void connect() {
        write("PASS " + oAuth);
        write("NICK " + nickname.toLowerCase());
        System.out.println("Connexion...");
    }

    public void join() {
        write("JOIN #" + chaine);
        on_join = true;
        System.out.println("Join...");
    }

    public void quit() {
        write("PART #" + chaine);
        on_join = false;
        System.out.println("Quit...");
    }

    public void write(String msg) {
        this.output.println(msg);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //fxml components prep
        channel_change_TextField.setText(chaine);

        try {
            //create connexion

            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) factory.createSocket("irc.chat.twitch.tv", port);


            socket.startHandshake();

            socket.setSoTimeout(2000);
            socket.setKeepAlive(true);

            output = new PrintWriter(socket.getOutputStream(), true);

            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //connect irc
            connect();

            Timeline timeline_connection = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
                try {
                    if (bufferedReader.ready()) {
                        String line = bufferedReader.readLine();

                        //join channel
                        if (line.contains("You are in a maze")) {
                            join();
                        }

                        if (line.contains("PING")) {
                            write("PONG :tmi.twitch.tv");
                        }

                        if (on_start && !line.contains("JOIN")
                                && !line.contains("PING") && !line.contains("NICK")
                                && !line.contains("PASS")
                                && !line.contains("/NAMES")
                        ) {
                            //init
                            Label pseudo = new Label(), msg = new Label(), date = new Label();
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                            VBox vBox = new VBox();
                            HBox hBox = new HBox();


                            //setstyle
                            vBox.setPrefWidth(370);
                            vBox.setMaxWidth(370);

                            vBox.setAlignment(Pos.CENTER_LEFT);

                            hBox.setPrefWidth(360);
                            hBox.setMaxWidth(360);
                            hBox.setMaxHeight(5);

                            vBox.setPadding(new Insets(3, 5, 3, 5));
                            hBox.setSpacing(5);
                            pseudo.getStyleClass().add("msg_pseudo");
                            msg.getStyleClass().add("msg_msg");
                            date.getStyleClass().add("msg_msg");
                            vBox.getStyleClass().add("msg_vbox");

                            pseudo.maxWidth(200);

                            msg.maxWidth(320);
                            msg.prefWidth(320);
                            msg.maxHeight(60);
                            msg.prefHeight(30);

                            date.setMaxWidth(60);
                            date.setMinWidth(30);

                            pseudo.setWrapText(true);
                            msg.setWrapText(true);


                            //split msg
                            String[] msg_split = line.substring(1).split(":");


                            //set text Label
                            msg.setText(msg_split[msg_split.length - 1]);
                            pseudo.setText(msg_split[0].split("!")[0] + ":");
                            date.setText(LocalDateTime.now().format(dateTimeFormatter));

                            //add
                            hBox.getChildren().addAll(date, pseudo);
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
            //tchat_msg_Vbox.heightProperty().addListener((ChangeListener<? super Number>) (observable, oldvalue, newValue) -> tchat_ScrollPane.setVvalue(1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close_button_Action(ActionEvent actionEvent) {
        try {
            if (this.socket != null) {
                this.socket.close();
                System.out.println("socket close");
            }
            if (this.bufferedReader != null) {
                this.bufferedReader.close();
                System.out.println("bufferReader close");
            }
            if (this.output != null) {
                this.output.close();
                System.out.println("PrintWriter close");
            }
            if (on_join) {
                quit();
            }
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void channel_change_Button_Action(ActionEvent actionEvent) {
        if (!on_channel_show) {

        } else {

        }
    }

    public void channel_change_TextField_Action(InputMethodEvent inputMethodEvent) {
        if (!channel_change_TextField.getText().isEmpty() && channel_change_TextField.getText().isBlank()) {
            channel_change_Button.setVisible(true);
        } else {
            channel_change_Button.setVisible(false);
        }

    }
}