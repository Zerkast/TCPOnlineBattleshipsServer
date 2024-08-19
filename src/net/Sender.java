package net;

import java.io.PrintWriter;

public class Sender {
    PrintWriter out;

    public Sender(PrintWriter writer) {
        this.out = writer;
    }
    
    public void send(String message){
        out.println(message);
        out.flush();
    }
    
    public void close(){
        out.close();
    }
}
