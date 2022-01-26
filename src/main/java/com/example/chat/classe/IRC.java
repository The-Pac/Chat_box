package com.example.chat.classe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class IRC {
    private PrintWriter output;
    private Socket socket;
    private BufferedReader bufferedReader;

    public IRC() {

    }

    public void start(String ip, int port, int timeout, boolean keepAlive, boolean autoFlush) {

        System.out.println("\u001B[33m" + "/////////////IRC/////////////");
        try {
            socket = new Socket(ip, port);
            socket.setSoTimeout(timeout);
            socket.setKeepAlive(keepAlive);

            System.out.println("\u001B[33m" + "Statut Socket NonSSL : " + socket.isConnected());


            output = new PrintWriter(socket.getOutputStream(), autoFlush);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read() {
        try {
            if (bufferedReader.ready()) {
                return bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void stop() {
        try {
            if (this.socket != null) {
                this.socket.close();
                System.out.println("\u001B[33m" + "socket close");
            }
            if (this.bufferedReader != null) {
                this.bufferedReader.close();
            }
            if (this.output != null) {
                this.output.close();
            }
            System.out.println("\u001B[33m" + "/////////////END IRC/////////////");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void password(String password) {
        write("PASS " + password);
        System.out.println("\u001B[33m" + "Connect with password...");
    }

    public void nickname(String nickname) {
        write("NICK " + nickname.toLowerCase());
        System.out.println("\u001B[33m" + "Connect with nickname: " + nickname);
    }

    public void join(String channel) {
        write("JOIN #" + channel);
        System.out.println("\u001B[33m" + "Joinning channel : " + channel);

    }

    public void quit(String channel) {
        write("PART #" + channel);
        System.out.println("\u001B[33m"+"Leaving channel : " + channel);
    }

    public void write(String msg) {
        this.output.println(msg);
    }
}
