package net;

import java.io.BufferedReader;
import java.io.IOException;

public class Receiver {
    boolean stop;
    String forClose;//la stringa che indica la fine delle letture
    InternalReceiver receiver;
    BufferedReader in;

    public Receiver(String forClose, BufferedReader in) {
        this.forClose = forClose;
        this.in = in;
        stop = false;
    }
    
    public void setConsumer(IMessageConsumer consumer){
        receiver = new InternalReceiver(in, consumer);
        receiver.start();
    }
    
    public void close(){
        stop = true;
    }
    
    class InternalReceiver extends Thread{
        BufferedReader in;
        private IMessageConsumer consumer;

        public InternalReceiver(BufferedReader in, IMessageConsumer consumer) {
            this.in = in;
            this.consumer = consumer;
        }
        
        @Override
        public void run(){
            String message;
            while(!stop){
                try {
                    message = in.readLine();
                    if(message!=null && !message.trim().isEmpty()){
                        consumer.consumeMessage(message);
                        if(message.equals(forClose)){
                            stop = true;
                            in.close();
                        }
                    } 
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
