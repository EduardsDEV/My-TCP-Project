import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Chatter class, contains a nickname and a socket of  client.
 *
 * Created by edwar on 10/2/2017.
 */
public class Chatter {

    private final Socket link;
    private final String nickname;
    private final Scanner input;
    private final PrintWriter output;
    private LocalDateTime latestIMAV = LocalDateTime.now();// IMAV

    /**
     * Chatter constructor is used to create a new chatter with a specified socket and nickname,
     * input/Scanner and output/PrintWriter are used to communicate with server.
     * @param link
     * @param nickname
     * @throws IOException
     */
    public Chatter(Socket link, String nickname) throws IOException{
        this.link = link;
        this.nickname = nickname;

        input = new Scanner(link.getInputStream());
        output = new PrintWriter(link.getOutputStream(), true);
    }

    public String getNickname() {
        return nickname;
    }

    public Scanner getInput() {
        return input;
    }

    public PrintWriter getOutput() {
        return output;
    }

    public Socket getLink() {
        return link;
    }

    //this is for IMAV msg
    public LocalDateTime getLatestIMAV() {
        return latestIMAV;
    }

    /**
     * this part is not finished yet, the idea is to store latestIMAV time so that
     * server can compare this time with now() time and see if he should kick chatter from server or keep him.
     */
    public void setLatestIMAV() {
        latestIMAV = LocalDateTime.now();
    }
}
