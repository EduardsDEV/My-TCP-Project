/**
 * Created by edwar on 10/3/2017.
 */
public class Services {

    public static boolean validJoinFormat(String message){
        if (message.startsWith("JOIN ") && message.contains(", ") && message.contains(":")){// also check for ", and ip and port" :D
            return true;
        }else return false;
    }
    public static boolean validDataFormat(String message){
        if (message.startsWith("DATA")){
            return true;
        }else return false;
    }
    public static boolean validQuitFormat(String message){
        if (message.equals("QUIT")){
            return true;
        }else return false;
    }


}
