package gameclosure.server;

import java.io.*;

/**
 *
 * @author js0044
 */
public class ChatMessage implements Serializable {

    protected static final long serialVersionUID = 1112122200L;

    /**
     * The different types of messages sent by client WHOISIN to receive a list
     * of what users are connected MESSAGE and ordinary message LOGOUT to
     * disconnect from server
     */
    static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2;
    private int type;
    private String message;

    //ChatMessage Constructor
    public void ChatMessage(int type, String message) {

        this.type = type;
        this.message = message;

    }

    //getters
    public int getType() {
        return type;

    }
    public String getMessage () {
    
        return message;
    
    }

}
