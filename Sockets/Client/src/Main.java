import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void sendMessage(String message,Socket clientSocket) throws IOException {

        OutputStream output = clientSocket.getOutputStream();

        PrintWriter writer = new PrintWriter(output,true);

        writer.println(message);

    }

    public static String receiveMessage(Socket clientSocket) throws IOException {

        InputStream input =  clientSocket.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        String message = reader.readLine();

        System.out.println(clientSocket);

        System.out.println(message);

        return message;
    }

    public static String getUserOption(){

        System.out.println("Welcome! Please select an option from the following:");
        System.out.println("1.Login");
        System.out.println("2.Register");
        System.out.println("3.Reset password");
        System.out.println("4.Exit");

        Scanner in = new Scanner(System.in);

        String option = in.nextLine();

        return option;
    }

    public static User getUserCredentials(){

        System.out.print("Please enter username: ");

        Scanner in = new Scanner(System.in);

        String username = in.nextLine();

        System.out.print("Please enter password: ");

        Scanner in1 = new Scanner(System.in);

        String password = in1.nextLine();

        return new User(username,password);
    }

    public  static User getUserCredentialsForPasswordRecovery()
    {
        System.out.print("Please enter username: ");

        Scanner in = new Scanner(System.in);

        String username = in.nextLine();

        return new User(username,"password");
    }

    public static void main(String[] args) throws IOException {

        InetAddress host = InetAddress.getLocalHost();

        boolean isOpen=true;

        while(isOpen) {

            Socket clientSocket = new Socket(host.getHostName(),8080);
            switch (getUserOption()) {

                case "1": {
                    System.out.println("Please enter the credentials!");

                    Message message = new Message(Options.LOGIN, getUserCredentials());

                    sendMessage(message.toString(), clientSocket);

                    if (receiveMessage(clientSocket).equals("Valid Credentials")) {

                        Boolean userlogged = true;

                        while (userlogged) {
                            System.out.println("Select an option:");
                            System.out.println("1.Log out");
                            System.out.println("2.Stay logged in");

                            Scanner in = new Scanner(System.in);

                            String option = in.nextLine();

                            switch (option) {
                                case "1": {
                                    Message message1 = new Message(Options.LOGOUT, message.getUser());

                                    sendMessage(message1.toString(), clientSocket);

                                    receiveMessage(clientSocket);

                                    userlogged = false;

                                    break;
                                }
                                case "2": {
                                    System.out.println("Stay logged in");
                                    break;
                                }
                                default: {
                                    System.out.println("Unknown option! Remain logged in");
                                    break;

                                }
                            }
                        }
                    } else {
                        System.out.println("Invalid Credentials");
                    }
                    break;
                }
                case "2": {
                    System.out.println("Please create a new user");

                    Message message = new Message(Options.SIGNIN, getUserCredentials());

                    sendMessage(message.toString(), clientSocket);

                    receiveMessage(clientSocket);

                    break;

                }
                case "3": {
                    System.out.println("Reset password");
                    Message message = new Message(Options.FORGOTPASSWORD, getUserCredentialsForPasswordRecovery());
                    sendMessage(message.toString(), clientSocket);
                    String response = receiveMessage(clientSocket);
                    if(response.equals("User exists"))
                    {
                        System.out.print("Please enter new password: ");

                        Scanner in = new Scanner(System.in);

                        String password = in.nextLine();
                        sendMessage(password,clientSocket);
                        receiveMessage(clientSocket);
                    }
                    else
                    {
                        System.out.println("User does not exist");
                    }
                    break;
                }
                case "4": {
                    System.out.println("Close connection");
                    isOpen = false;
                    Message message=null;
                    sendMessage(message.toString(),clientSocket);
                    clientSocket.close();
                    break;
                }
                default: {
                    System.out.println("Unknown option");
                }

            }
        }
    }
}