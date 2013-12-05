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
        if (sg == null) {
            System.out.println(messageLf);
        } else {
            sg.appendRoom(messageLf); //append in the Room Window
        }
        // we will loop in reverse in case we have to remove a client
        // that has been disconnected

        for (int i = ct.size(); --i >= 0;) {

            ClientThread ctt = ct.get(i);
            //try to write to the client, if it fails remove it from the list

            if (!ctt.writeMessage(messageLf)) {

                ct.remove(i);
                dispaly("Disconnected client " + ct.username + "removed from list");

            }

        }
    }

    //for client that logs off using LOGOUT message
    synchronized void remove(int id) {

        //scan the ArrayList until we find the id
        for (int i = 0; i < ct.size(); i++) {
            ClientThread cttt = ct.get(i);
            //found it
            if (ct.id == id) {
                cttt.remove(i);
            }
            return;

        }

    }

    /**
     * To run as a console application type >java Server > java Server
     * portNumber
     *
     * If port Number is not specified 1500 is used
     */
    public static void main(String[] args) {

//start server on port 1500
        int portNumber = 1500;

        switch (args.length) {
            case 1:
                try {
                    portNumber = Integer.parseInt(args[0]);

                } catch (Exception e) {
                    System.out.println("Invalid port number");
                    System.out.println("Usage is: > java Server [portnumber]");
                    return;

                }
            case 0:
                break;

            default:
                System.out.println("Usage is: > java Server [portnumber]");
                return;

        }//creat a server object and start it
        Server server = new Server(portNumber);
        server.start();
    }

    /**
     * One instance of this thread will run for each client
     */
    class ClientThread extends Thread {

        //the socket to pay attention too
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;

        //my unique id easy for disconnection
        int id;

        //the username of the client
        String username;

        //the only type of message he will receive
        ChatMessage cm;

        //the date I connect
        String date;

        //Constructor
        ClientThread(Socket socket) {
            //unique id
            id = ++uniqueId;
            this.socket = socket;

            //creating both data stream
            System.out.println("System creating Input/Output streams");

            try {

                //create output first
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                //read the username

                username = (String) sInput.readObject();
                display(username + " Just connected.");

            } catch (IOException e) {
                display("Exception occured creatin Input/Output Streams " + e);
                return;
            } //must catch class not found exception
            //but we read a string so it should work
            catch (ClassNotFoundException e) {

            }
            date = new Date().toString() + "\n";
        }
        
        //what will run forever
        
        public void run( ) {
        // to loop until LOGOUT
            boolean keepGoing = true;
            while (keepGoing) {
            //read String which is an object
                
            
            
            
            }
        
        
        }
        

    }

}
