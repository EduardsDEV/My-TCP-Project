import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by edwar on 10/1/2017.
 */
public class Client {
    private String host;
    private int port;
    private String nickname;


    public static void main(String[] args) throws IOException {

        new Client("127.0.0.1", 4444).run();
    }

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws IOException {
        Socket socket = new Socket("127.0.0.1", 4444);
        Scanner sc = new Scanner(System.in);
        System.out.println("enter nickname: ");
        nickname = sc.nextLine();
        Scanner dataIn = new Scanner(socket.getInputStream());
        PrintWriter dataOut = new PrintWriter(socket.getOutputStream(), true);
        String join = "JOIN " + nickname + ", " + host + ":" + port;
        dataOut.println(join);
        String answer = dataIn.nextLine();
        System.out.println(answer);
        while(answer.contains("402")){          // this one offers to type another nickname;
            System.out.println("Please choose another nickname");
            nickname = sc.nextLine();
            join = "JOIN " + nickname + ", " + host + ":" + port;
            dataOut.println(join);
            answer = dataIn.nextLine();

        }
        Chatter chatter = new Chatter(socket, nickname);

        System.out.println("type messages: ");

        Thread t = new Thread(() -> {
            while (true) {
                String msg;
                String input = sc.nextLine();
                if (input.equals("*q")) {
                    msg = "QUIT";
                    try {
                        dataOut.println(msg);
                        socket.close();
                        System.exit(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    msg = "DATA " + nickname + ": " + input;
                }
                dataOut.println(msg);

            }
        });
        t.start();
        Thread t2 = new Thread(() -> {
            while (true) {
                String message = dataIn.nextLine();
                if (message.startsWith("DATA")) {
                    message = message.substring(5);
                }
                System.out.println(message);
            }
        });
        t2.start();

    }
}
