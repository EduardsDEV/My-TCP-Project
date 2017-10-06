import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Created by edwar on 10/2/2017.
 */
public class Chatter {

    private final Socket link;
    private final String nickname;
    private final Scanner input;
    private final PrintWriter output;
    private LocalDateTime latestIMAV = LocalDateTime.now();// IMAV

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

    public void setLatestIMAV(LocalDateTime latestIMAV) {
        latestIMAV = LocalDateTime.now();
    }
}
