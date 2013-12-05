package gameclosure.server;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Server can run on the prompt or as a GUI
 */
/**
 *
 * @author js0044
 */
public class Server {

    // Unique ID for each connection
    private static int uniqueId;

    // keep a list of clients connected
    private ArrayList<ClientThread> ct;

    // if using GUI
    private ServerGUI sg;

    // to display time
    private SimpleDateFormat sdf;

    // the port number to listen to 
    private int port;

    // The boolean that will be turned off when the server stops
    private boolean keepGoing;

    /**
     * The server constructor that receives the port number to listen to through
     * a command line parameter. The first parameter after the $java Server
     * command
     *
     */
    public Server(int port) {
        //keep an eye on this line...may cause problems
        this(port, null);

    }

    public Server(int port, ServerGUI sg) {
        //the port
        this.port = port;

        //GUI or not
        this.sg = sg;

        //to display hh:mm:ss
        sdf = new SimpleDateFormat("HH:mm:ss");

        //ArrayList for client list
        ct = new ArrayList<ClientThread>();

    }

    public void start() {

        keepGoing = true;

        /* create socket server and wait for request*/
        try {

            while (keepGoing) {
                //format waiting message
                display("Waiting for clients on port " + port + ".");

                Socket socket = serverSocket.accept(); //accept connection

                //if asked to stop
                if (!keepGoing) {
                    break;
                }

                //create new thread 
                ClientThread t = new ClientThread(socket);

                //save it in ArrayList
                ct.add(t);
                t.start();

            }
            // I was asked to stop
            try {
                serverSocket.close();
                for (int i = 0; i < ct.size(); i++) {
                    ClientThread co = ct.get(i);
                    try {
                        co.sInput.close();
                        co.sOutput.close();
                        co.sSocket.close();
                    } catch (IOException ioE) {
                        //nothing you can do really

                    }

                }

            } catch (Exception e) {

                display("Closing the Server and client: " + e);

            }

        } // something went bad
        catch (Exception e) {

            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            display(msg);

        }
    }
// for the GUI to stop the server

    protected void stop() {

        keepGoing = false;
        //connect to myself as Client to exit statement
        //Socket socket = serverSocket.accept();

        try {
            new Socket("localhost", port);

        } catch (Exception e) {
            //nothing you can do really
        }

    }
//display an event (not a message) to the console of GUI

    private void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg;
        if (sg == null) {
            System.out.println(time);
        } else {
            sg.appendEvent(time + "\n");
        }

    }
    //to broadcast event to all clients

    private synchronized void broadcast(String message) {

        // Add HH:mm:ss \n to the message
        String time = sdf.format(new Date());
        String messageLf = time + " " + message + "\n";
        
        // display message on console or GUI
        if (sg == null) System.out.println(messageLf);
        else sg.appendRoom(messageLf);
    }
}
