import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Server class, has a PORT nr, HashSet of nicknames
 * in main() runs the runServer(). runServer() creates ServerSocket
 * and waits for multiple amount of clients to connect, calls the ServerThread thread
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
        //System.out.println("Server up and ready for connections on port: " + PORT);
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

    /**
     * adds the client to the nicknames HashSet,
     * updates a String with all the chatters nicknames,
     * sends updated list to all the chatters.
     * Creates a thread using lambda expression to handle messages from client(DATA, QUIT, IMAV).
     * @param chatter
     */
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
                if (Services.validDataFormat(message)) {
                    System.out.println(message);//printing client message
                    Server.broadcastMsg(message);
                    continue;
                }

                //-------------------------------------------------------------------------------------------------------------
                //now QUIT part
                if (Services.validQuitFormat(message)) {
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
                    //out.println("you are alive!");
                    continue;
                }
                System.out.println("Debug: " + message);    // this is to print anything else that coming to server, left for debugging
                out.println("J_ER 405: better luck next time");
            }
        });
        t.start();

        /*
imav thread checks latestIMAV from chatter in Chatter class, and if
difference betwwen it and now() is more than a minute it calls removeClient().
This functionality works, but not properly: it removes inactive client and sends an updated list to
participants, but exceptions, such as java.util.ConcurrentModificationException  and java.util.NoSuchElementException: No line found
are thrown. This thread still needs to be improved.
 */

//            Timer timer = new Timer(true);
//            timer.scheduleAtFixedRate(new TimerTask() {
//                @Override
//                public void run() {
//                    for (Chatter c : nicknames) {
//
//                        //now the IMAV part
//                        LocalDateTime nowMinusMinute;
//                        nowMinusMinute = LocalDateTime.now().minusMinutes(1);
//                        if (nowMinusMinute.isAfter(chatter.getLatestIMAV())) {
//                            removeClient(chatter);
//                        }
//                    }
//                }
//            }, 10000, 30*1000);



    }

    /**
     * Removes the client form the "nicknames" HashSet
     * updates the String containing all the nicknames
     * sends an updatet String to all the chatters(LIST <n1><n2>...)
     * @param chatter
     */
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

    /**
     * broadcasts message to all the chatters from nicknames HashSet
     * @param message
     */
    public static synchronized void broadcastMsg(String message) {
        for (Chatter c : nicknames) {
            c.getOutput().println(message);
        }
    }

    /**
     * checks if nickname(parameter) is not used already in the nicknames HashSet
     *
     * @param nickname
     * @return
     * true if is not used
     */
    public static boolean isNotUsed(String nickname) {
        for (Chatter c : nicknames) {
            if (c.getNickname().equals(nickname)) {
                return false;
            }
        }
        return true;
    }
}
