import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by edwar on 10/1/2017.
 */
public class Server {

    private static volatile Set<Chatter> nicknames = new HashSet<>();

    public static final int PORT = 4444;

    public static void main(String[] args) throws IOException {
        new Server().runServer();
    }

    public void runServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server up and ready for connections on port: " + PORT);
        System.out.println("#--------------#");
        System.out.println("#Server Running#");
        System.out.println("#**************#");
        System.out.println("#   on " + PORT +"    #");
        System.out.println("#**************#");
        System.out.println("#--------------#");
        while (true) {
            Socket socket = serverSocket.accept();
            new ServerThread(socket).start();
            //checkClient();
        }
    }

    public static synchronized void addClient(Chatter chatter) {
        nicknames.add(chatter);
        String fullNicknamesList = "";
        for (Chatter c : nicknames) {
            fullNicknamesList = fullNicknamesList + c.getNickname() + " ";
        }

        for (Chatter c : nicknames) {
            // get the printwriter from the socket which is saved under the chatter object
            c.getOutput().println("LIST " + fullNicknamesList);
        }
        Thread t = new Thread(() -> {
            Scanner sc = chatter.getInput();
            PrintWriter out = chatter.getOutput();
            while (true) {
                String message = sc.nextLine();
                //now the DATA part
                if (message.startsWith("DATA")) {
                    System.out.println(message);//printing client message
                    Server.broadcastMsg(message);
                    continue;
                }

                //-------------------------------------------------------------------------------------------------------------
                //now QUIT part
                if (message.equals("QUIT")) {
                    removeClient(chatter);
                    System.out.println(message + " " + chatter.getNickname());
                    try {
                        chatter.getLink().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }
                if(message.equals("IMAV"))
                {
                    System.out.println(message); //printing client IMAV message
                    out.println("you are alive!");
                    continue;
                }
                System.out.println("Debug: " + message);
                out.println("J_ER 405: better luck next time");
            }
        });
        t.start();


    }

    public static synchronized void removeClient(Chatter chatter) {
        nicknames.remove(chatter);
        String fullNicknamesList = "";
        for (Chatter c : nicknames) {
            fullNicknamesList = fullNicknamesList + c.getNickname() + " ";
        }

        for (Chatter c : nicknames) {
            // get the printwriter from the socket which is saved under the chatter object
            c.getOutput().println("LIST " + fullNicknamesList);
        }
    }

    public static synchronized void broadcastMsg(String message) {
        for (Chatter c : nicknames) {
            c.getOutput().println(message);
        }
    }

    public static synchronized void checkClient() {
        Thread t = new Thread(() -> {
            for (Chatter c : nicknames) {
                c.getOutput().println("");
                if (c.getOutput().checkError()) {
                    c.getInput().close();
                    removeClient(c);
                }
            }
        });
    }

    public static boolean isNotUsed(String nickname) {
        for (Chatter c : nicknames) {
            if (c.getNickname().equals(nickname)) {
                return false;
            }
        }
        return true;
    }
}
