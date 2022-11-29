package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EchoThread extends Thread {
    protected Socket socket;

    public static String receiveMessage(Socket serverSocket) throws IOException {

        InputStream input =  serverSocket.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        String message = reader.readLine();

        System.out.println(serverSocket);

        return message;
    }

    public static Message convertToMessageObject(String message){

        String[] arrOfStr = message.split("_");

        return new Message(arrOfStr[0],new User(arrOfStr[1],arrOfStr[2]));
    }

    public static void sendMessage(String message,Socket serverSocket) throws IOException {
        OutputStream output = serverSocket.getOutputStream();

        PrintWriter writer = new PrintWriter(output,true);

        writer.println(message);
    }

    public static List<User> createListOfUsers(){
        List<User> list = new ArrayList<User>();
        User user1 = new User("user","admin");
        User user2 = new User("mihai","parola");
        list.add(user1);
        list.add(user2);
        return list;
    }

    public static boolean userHasValidCredentials(User userFromClient) throws IOException {

        Gson gson = new Gson();

        Reader reader = Files.newBufferedReader(Paths.get("src/main/java/org/example/users.json"));

        List<User> users = new Gson().fromJson(reader, new TypeToken<List<User>>() {}.getType());

        users.forEach(System.out::println);

        reader.close();
        for(User u:users)
        {
            if(u.getUsername().equals(userFromClient.getUsername())
                    && u.getPassword().equals(userFromClient.getPassword()))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean ifExists(User userFromClient) throws IOException {

        Gson gson = new Gson();

        Reader reader = Files.newBufferedReader(Paths.get("src/main/java/org/example/users.json"));

        List<User> users = new Gson().fromJson(reader, new TypeToken<List<User>>() {}.getType());

        users.forEach(System.out::println);

        reader.close();

        for (User u : users) {
            if (u.getUsername().equals(userFromClient.getUsername())) {
                return true;
            }
        }
        return false;
    }

    private static List<User> users;
    public EchoThread(Socket clientSocket) throws IOException {

        this.socket = clientSocket;

        //this.users  = createListOfUsers();
    }

    private static void Register(User user) throws IOException {

        Reader reader = Files.newBufferedReader(Paths.get("src/main/java/org/example/users.json"));

        List<User> users = new Gson().fromJson(reader, new TypeToken<List<User>>() {}.getType());

        reader.close();

        users.add(user);

       try(Writer writer = new FileWriter("src/main/java/org/example/users.json")) {

           Gson gson = new GsonBuilder().create();
           gson.toJson(users, writer);

       }

    }

    private static void changePassword(User user,String newPasword) throws IOException {

        Reader reader = Files.newBufferedReader(Paths.get("src/main/java/org/example/users.json"));

        List<User> users = new Gson().fromJson(reader, new TypeToken<List<User>>() {}.getType());

        reader.close();

        for(User u:users)
        {
            if(u.getUsername().equals(user.getUsername()))
            {
                u.setPassword(newPasword);
            }
        }

        try(Writer writer = new FileWriter("src/main/java/org/example/users.json")) {

            Gson gson = new GsonBuilder().create();
            gson.toJson(users, writer);

        }
    }

    public void run() {

        System.out.println("Waiting for client...");

        while(true) {

            try {

                Message message = convertToMessageObject(receiveMessage(socket));

                if (message == null) {
                    socket.close();
                } else {

                    switch (message.getContent()) {
                        case "LOGIN": {
                            if (userHasValidCredentials(message.getUser()) == true) {
                                sendMessage("Valid Credentials", socket);

                                Message messageAfterLogin = convertToMessageObject(receiveMessage(socket));

                                System.out.println(messageAfterLogin.getContent());

                                System.out.println("User " + messageAfterLogin.getUser().getUsername() + " logged out");

                                sendMessage("Log out successfuly", socket);
                            } else {
                                sendMessage("Invalid Credentials", socket);
                            }
                            break;
                        }
                        case "SIGNIN": {
                            if (ifExists(message.getUser()) == false) {
                                Register(message.getUser());
                                sendMessage("User registration complete", socket);
                            } else {
                                sendMessage("User already exists", socket);
                            }
                            break;
                        }
                        case "FORGOTPASSWORD": {
                            if (ifExists(message.getUser()) == true) {
                                sendMessage("User exists", socket);

                                String newPassword = receiveMessage(socket);

                                changePassword(message.getUser(),newPassword);
//
                                sendMessage("Password changed successfully", socket);
                            } else {
                                sendMessage("User does not exist", socket);
                            }
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected");
                System.out.println("___________________");
                break;
            }
        }
    }
}