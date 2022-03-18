import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    private static Socket client; //сокет для общения
    private static BufferedReader input; // поток чтения из сокета
    private static BufferedWriter output; // поток записи в сокет
    private static int PORT = 2222;

    public static void main(String[] args){
        try {
            client = new Socket("helios.se.ifmo.ru", PORT);
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            System.out.println("Подключено");

            Scanner sc = new Scanner(System.in);
            Gson gson = new Gson();
            while (true){
                try {
                    String[] splittedCommand = sc.nextLine().split(" ");
                    Command command = new Command(CommandType.valueOf(splittedCommand[0]), Arrays.copyOfRange(splittedCommand, 1, splittedCommand.length));
                    if (command.getType() == CommandType.exit){
                        client.close();
                        input.close();
                        output.close();
                        System.exit(0);
                    }
                    send(gson.toJson(command));
                    for (String s : gson.fromJson(input.readLine(), Message.class).getMessage())
                        System.out.println(s);
                } catch (IllegalArgumentException ex) {
                    System.out.println("Нет такого типа команды. Команда должна быть одним из следующих типов:");
                    for (CommandType ct : CommandType.values())
                        System.out.println(ct + ", ");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void send(String message){
        try {
            output.write(message + '\n');
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
