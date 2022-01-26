package com.example.chat;

import com.example.chat.classe.IRC;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public String oAuth = "oauth:oig27sm2xk21wk9yg7nv84jk428ugl",
            chaine = "at0mium", nickname = "pacreported";
    public int port = 6667;
    public IRC irc;
    public boolean on_start = false, on_join = false, on_channel_show = false;

    @FXML
    public HBox statut_Hbox, channel_change_hbox;
    public VBox tchat_msg_Vbox;
    public ScrollPane tchat_ScrollPane;
    public TextField channel_change_TextField;
    public Button close_button, channel_change_Button;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //fxml components prep

        //create connexion
        irc = new IRC();
        irc.start("irc.chat.twitch.tv", port, 1000, true, true);
        irc.password(oAuth);
        irc.nickname(nickname);


        Timeline timeline_connection = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
            String reader_value = irc.read();
            if (reader_value != null) {
                if (!on_join && reader_value.contains("You are in a maze")) {
                    irc.join(chaine);
                    on_join = true;
                }
                if (on_start && !reader_value.contains("JOIN")
                        && !reader_value.contains("PING")
                        && !reader_value.contains("NICK")
                        && !reader_value.contains("PART")
                        && !reader_value.contains("PASS")
                        && !reader_value.contains("/NAMES")
                ) {
                    if (!reader_value.substring(1).split(":")[0].split("!")[0].contains("tmi.twitch.tv")) {
                        create_message_box(reader_value);
                    }
                    if (tchat_msg_Vbox.getChildren().size() > 5) {
                        tchat_msg_Vbox.getChildren().remove(0, 3);
                    }
                }
                if (!on_start && reader_value.split(":")[2].contains(">")) {
                    on_start = true;
                }
            }
        }));
        timeline_connection.setCycleCount(Animation.INDEFINITE);
        timeline_connection.play();
        //tchat_msg_Vbox.heightProperty().addListener((ChangeListener<? super Number>) (observable, oldvalue, newValue) -> tchat_ScrollPane.setVvalue(1));

    }

    public void create_message_box(String value) {
        //init
        Label pseudo = new Label(), msg = new Label(), date = new Label();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        VBox container_vBox = new VBox();
        HBox pseudo_date_hbox = new HBox();


        //setstyle
        container_vBox.setMinSize(370, 50);
        container_vBox.setMaxWidth(370);

        container_vBox.setAlignment(Pos.CENTER_LEFT);
        pseudo_date_hbox.setAlignment(Pos.CENTER_LEFT);

        pseudo_date_hbox.setPrefWidth(370);
        pseudo_date_hbox.setMaxHeight(10);

        container_vBox.setPadding(new Insets(3, 5, 3, 5));
        pseudo_date_hbox.setSpacing(5);
        pseudo.getStyleClass().add("msg_pseudo");
        msg.getStyleClass().add("msg_msg");
        date.getStyleClass().add("msg_msg");
        container_vBox.getStyleClass().add("msg_vbox");

        pseudo.setMaxWidth(200);

        msg.setPrefWidth(360);
        msg.setWrapText(true);

        date.setMaxWidth(60);
        date.setMinWidth(30);


        //split msg
        String[] msg_split = value.substring(1).split(":", 2);


        //set text Label
        msg.setText(msg_split[msg_split.length - 1]);
        pseudo.setText(msg_split[0].split("!")[0] + ":");
        date.setText(LocalDateTime.now().format(dateTimeFormatter));

        //add
        pseudo_date_hbox.getChildren().addAll(date, pseudo);
        container_vBox.getChildren().addAll(pseudo_date_hbox, msg);
        tchat_msg_Vbox.getChildren().add(container_vBox);
    }

    public void close_button_Action() {
        irc.quit(chaine);
        irc.stop();
    }

    public void channel_change_Button_Action() {
        if (!on_channel_show) {
            on_channel_show = true;
            channel_change_TextField = new TextField();
            channel_change_hbox.getChildren().add(channel_change_TextField);
            channel_change_TextField.textProperty().addListener((observable, oldValue, newValue) -> channel_change_Button.setVisible(newValue == null || newValue.equals("")));
            channel_change_TextField.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    if (!channel_change_TextField.getText().isEmpty() && !channel_change_TextField.getText().isBlank()) {
                        if (on_join) {
                            irc.quit(chaine);
                            chaine = channel_change_TextField.getText();
                            on_join = false;
                            tchat_msg_Vbox.getChildren().clear();
                        }
                        irc.join(channel_change_TextField.getText());
                        channel_change_hbox.getChildren().remove(channel_change_TextField);
                        channel_change_Button.setVisible(true);
                    }
                }
            });
        } else {
            channel_change_hbox.getChildren().remove(channel_change_TextField);
            on_channel_show = false;
        }
    }
}