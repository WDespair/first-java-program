package org.example.utils;


import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatClient {
    private static String CHAT_SERVER_URL = "http://localhost:8080/chat/chat";
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static Gson gson = new Gson();

    public static void main(String[] args) throws Exception {

        if (args.length != 1) {
            System.err.println("Usage: java ChatClient <username>");
            return;
        }

        String user = args[0];
        System.out.println(user);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                receiveMessage(user);
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }, 0, 1, TimeUnit.SECONDS);

        try (Scanner sc = new Scanner(System.in)){
            while (true){
                String message = sc.nextLine();
                sendMessage(user, message);
            }
        }
    }

    private static void sendMessage(String user, String message) throws IOException {
        try {
            URL url = new URL(CHAT_SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

            String data = "user=" + user + "&message=" + URLEncoder.encode(message, "UTF-8");

            try (OutputStream os = connection.getOutputStream()) {
                os.write(data.getBytes("UTF-8"));
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println(connection.getResponseCode());
            }


        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void receiveMessage(String user) throws IOException {
        try {
            URL url = new URL(CHAT_SERVER_URL + "?user=" + URLEncoder.encode(user, "UTF-8"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {

                    String json = reader.readLine();
                    String[] messages = gson.fromJson(json, String[].class);

                    for (String m : messages) {
                        System.out.println(m);
                    }
                }
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}