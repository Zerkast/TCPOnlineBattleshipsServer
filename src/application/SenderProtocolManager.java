package application;
import net.Sender;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import data.UserData;

public class SenderProtocolManager {
    private HashMap<Integer, String> errors;
    private Sender sender;
    // private String lastMessageSent; //PER RIMANDARE LA STRINGA IN CASO DI ERRORI, MA NON SERVE PERCHÈ L'AFFIDABILITÀ È GARANTITA DA TCP

    public SenderProtocolManager(Sender sender) {
        this.sender = sender;
        errors =  new HashMap<>();
        errors.put(0, "unknown command");
        errors.put(1, "illegal arguments");
        errors.put(2, "username already taken");
        // errors.put(3, "game already started"); //TODO:ERRORI OPZIONALI
        // errors.put(4, "not allowed");
    }

    public void sendOutcome(String command, boolean outcome) {
        sender.send(command+"#"+translateBoolean(outcome));
    }

    public void sendError(int errorCode) {
        sender.send("error#"+errors.get(errorCode));
    }

    public void sendError(String errorMessage) { //PER QUANDO PASSA ERRORI GIA COSTRUITI DEI CLIENT
        sender.send("error#"+errorMessage);
    }

    public void close() {
        sender.close();
    }

    public void updateUserList(Set<Entry<String, UserData>> usersConnected) { 
        String message = "users#";
        for (Entry<String, UserData> user : usersConnected) {
            message+=user.getKey()+"@"+translateBoolean(user.getValue().getStatus())+"&";
        }
        message = message.substring(0, message.length()-1);
        sender.send(message);
        System.out.println(message);
    }

    public void sendShot(int posX, int posY) {
        sender.send("shoot#"+posX+"&"+posY);
    }

    public void sendInvite(String username) {
        sender.send("invite#"+username);
    }

    public void sendInviteResponse(String username, boolean response) {
        sender.send("inviteResponse#" + username+ "&"+translateBoolean(response));
    }

    public void sendGameReadyNotification() {
        sender.send("gameReady#");
    }

    public void sendGameEndNotification(boolean isVictory) {
        sender.send("gameEnd#"+translateBoolean(isVictory));
    }

    public void sendGameStartNotification(String username) {
        sender.send("gameStart#"+username);
    }

    public void sendKeepAlive() {
        sender.send("hi#");
    }

    private String translateBoolean(boolean bool) {
        String msg = "f";
        if (bool) msg = "t";
        return msg;

    }
}
