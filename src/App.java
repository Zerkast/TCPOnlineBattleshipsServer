import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import application.*;
import data.UserData;


import net.IMessageConsumer;
import net.Receiver;
import net.Sender;

public class App {
    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(60000);
        Map<String, UserData> connectedUsers = new HashMap<>();
        while (true) {
            Socket s = ss.accept();
            s.setKeepAlive(true);
            PrintWriter out = new PrintWriter(s.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            System.out.println("client " + s.getInetAddress() + " " + s.getPort() + " connected");
            Sender sender = new Sender(out);
            SenderProtocolManager spm = new SenderProtocolManager(sender);
            ICommandConsumer app = new Server(connectedUsers, spm);
            IMessageConsumer receiver = new ReceiverProtocolManager(app, spm);
            Receiver rec = new Receiver("quit", in);
            rec.setConsumer(receiver);
        }
    }
}
