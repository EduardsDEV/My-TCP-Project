import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Client class, connects to server, sends join,
 * and starts 3 more threads, 1 for sending smgs, 2 for receiving,
 * 3rd for sending IMAV.
 * <p>
 * Created by edwar on 10/1/2017.
 */
public class Client {
    private String host;
    private int port;
    private String nickname;


    public static void main(String[] args) throws IOException {

        new Client("172.16.27.152", 1448).run();
    }

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws IOException {
        Socket socket = new Socket("172.16.27.152", 1448);
        Scanner sc = new Scanner(System.in);
        System.out.println("enter nickname: ");
        nickname = sc.nextLine();
        Scanner dataIn = new Scanner(socket.getInputStream());
        PrintWriter dataOut = new PrintWriter(socket.getOutputStream(), true);
        String join = "JOIN " + nickname + ", " + host + ":" + port;
        dataOut.println(join);
        String answer = dataIn.nextLine();
        System.out.println(answer);
        while (answer.contains("402")) {          // this one offers to type another nickname;
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
                if (input.length() < 250) {

                    dataOut.println(msg);
                } else {
                    System.out.println("msg too long");
                }

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
        /**
         * this thread is sending IMAV msg every x seconds
         */
        Timer timer = new Timer(true);
//            timer.scheduleAtFixedRate(timerTask, 0, 10 * 1000);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                chatter.setLatestIMAV();
                dataOut.println("IMAV");
            }
        }, 10000, 03 * 1000);
    }


}
