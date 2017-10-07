import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * ServerThread thread, is called in Server class when serverSocket accepts a new client.
 *
 * Created by edwar on 10/1/2017.
 */
public class ServerThread extends Thread {
    Socket socket;

    ServerThread(Socket socket) {
        this.socket = socket;
    }

    /**
     * in this method, communication between client and server starts, on a server side.
     * as 1st msg from client should be "JOIN..." msg, this method checks if msg is following the protocol.
     * If all checks passed, J_OK is sent and Server.addClient(...) is called.
     */
    public void run() {
        try {
            Scanner sc = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            String message = sc.nextLine();
            if (message.startsWith("JOIN")) {
                // check join format, if wrong format send J_ER
                System.out.println(message);//print JOIN MESSAGE
                if (!Services.validJoinFormat(message)) {
                    out.println("J_ER 400: Wrong JOIN format");
                    socket.close();
                    return;
                }
                String nickname;                            // from message get the nickname and store in a variable
                nickname = message.substring(5).split(",")[0];
                UsernameValidator check = new UsernameValidator();  // validation on nickname format

                if (!check.validate(nickname)) {
                    out.println("J_ER 401: Wrong nickname format");
                    socket.close();
                    return;
                }
                while (!Server.isNotUsed(nickname)) {               // check if nickname is unique, that said no duplication
                                                                    // "while" because server needs to offer client a chance to try another nickname
                    out.println("J_ER 402: Duplicate nickname");
                    //socket.close();
                    //return;
                    nickname=sc.nextLine().substring(5).split(",")[0];
                }                                      // when it gets to this line, the client is eligible to use the chatting system
                out.println("J_OK");

                Server.addClient(new Chatter(socket, nickname));  // sendList()
                //
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
