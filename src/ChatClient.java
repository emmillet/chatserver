
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class ChatClient {
    private static Socket socket;
    private static BufferedReader socketIn;
    private static PrintWriter out;
    public static String initialStatus;

    public static void main(String[] args) throws Exception {
        Scanner userInput = new Scanner(System.in);

        System.out.println("What's the server IP? ");
        String serverip = userInput.nextLine();
        System.out.println("What's the server port? ");
        int port = userInput.nextInt();
        userInput.nextLine();

        socket = new Socket(serverip, port);
        socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // start a thread to listen for server messages
        ServerListener listener = new ServerListener();
        Thread t = new Thread(listener);
        t.start();

        System.out.print("Chat sessions has started - enter a user name: ");
        String name = userInput.nextLine().trim();
        out.println(name); //out.flush();

        String status = "";
        System.out.println("Please enter a status: ");
        initialStatus = userInput.nextLine().trim();
        out.println(initialStatus);

        String line = userInput.nextLine().trim();
        while(!line.toLowerCase().startsWith("/quit")) {
            if(line.toLowerCase().startsWith("/status")){
                status = String.format("STATUS %s", line.toLowerCase().substring(8));
                out.println(status);

                line = userInput.nextLine().trim();
                continue;
            }
            else if(line.startsWith("@")){
                int index = 1;
                while(line.charAt(index) != ':'){
                    index++;
                }
                String user = line.substring(1, index +1);
                String dm = String.format("DM%s %s", user, line.substring(index+1));
                out.println(dm);
                line = userInput.nextLine().trim();
                continue;
            }
            String msg = String.format("CHAT %s", line);
            out.println(msg);
            line = userInput.nextLine().trim();
        }
        out.println("QUIT");
        out.close();
        userInput.close();
        socketIn.close();
        socket.close();

    }

    static class ServerListener implements Runnable {

        @Override
        public void run() {
            try {
                String incoming = "";

                while( (incoming = socketIn.readLine()) != null) {
                    //handle different headers
                    //WELCOME
                    //CHAT
                    //EXIT
                    System.out.println(incoming);
                }
            } catch (Exception ex) {
                System.out.println("Exception caught in listener - " + ex);
            } finally{
                System.out.println("Client Listener exiting");
            }
        }
    }
}