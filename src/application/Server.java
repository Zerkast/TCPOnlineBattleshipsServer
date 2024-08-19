package application;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import data.UserData;

public class Server implements ICommandConsumer{
    private Map<String, UserData> connectedUsers;
    SenderProtocolManager senderProtocolManager;
    private String currentUsername;
    private boolean isStillConnected;
    private AtomicBoolean keepAliveFlag; 
    public Server(Map<String, UserData> connectedUsers, SenderProtocolManager currentSpm) {
        keepAliveFlag = new AtomicBoolean(true);
        this.connectedUsers = connectedUsers;
        senderProtocolManager = currentSpm;
        new InnerKeepAliveServer().start();
        new InnerKeepAliveClient().start();
    }

    @Override
    public void join(String username) {
        if (connectedUsers.containsKey(username) || username.length()>=25) {
            senderProtocolManager.sendOutcome("join", false);
            senderProtocolManager.sendError(2);
        } else {
            currentUsername = username;
            senderProtocolManager.sendOutcome("join", true);
            connectedUsers.put(username, new UserData(senderProtocolManager));
            for (var entry : connectedUsers.entrySet()) {
                entry.getValue().getSenderProtocolManager().updateUserList(connectedUsers.entrySet());
            }
        }
        setKeepAlive();
    }

    @Override
    public void quit() {
        keepAliveFlag.set(false);
        if (currentUsername!=null && connectedUsers.containsKey(currentUsername)) {
            System.out.println(currentUsername + " disconnected");
            connectedUsers.remove(currentUsername);
            for (var entry : connectedUsers.entrySet()) {
                entry.getValue().getSenderProtocolManager().updateUserList(connectedUsers.entrySet());
            }
        }
        senderProtocolManager.close();
    }

    @Override
    public void setKeepAlive() {
        isStillConnected = true;
    }

    @Override
    public void notifyGameStart(String username) {
        connectedUsers.get(currentUsername).setStatus(true);
        connectedUsers.get(username).setStatus(true);
        connectedUsers.get(username).getSenderProtocolManager().sendGameStartNotification(currentUsername);
        for (var entry : connectedUsers.entrySet()) {
            entry.getValue().getSenderProtocolManager().updateUserList(connectedUsers.entrySet());
        }
        setKeepAlive();
    }

    @Override
    public void sendInvite(String username) {
        connectedUsers.get(username).getSenderProtocolManager().sendInvite(currentUsername);
        setKeepAlive();
    }

    @Override
    public void sendInviteResponse(String username, boolean hasAccepted) {
        connectedUsers.get(username).getSenderProtocolManager().sendInviteResponse(currentUsername, hasAccepted);
        setKeepAlive();
    }

    @Override
    public void notifyGameReady(String username) {
        connectedUsers.get(username).getSenderProtocolManager().sendGameReadyNotification();
        setKeepAlive();
    }

    @Override
    public void shoot(int posX, int posY, String username) {
        connectedUsers.get(username).getSenderProtocolManager().sendShot(posX, posY);
        setKeepAlive();
    }

    @Override
    public void shootOutcome(boolean hasHit, String username) {
        connectedUsers.get(username).getSenderProtocolManager().sendOutcome("shootResponse", hasHit);
        setKeepAlive();
    }


    @Override
    public void notifyGameEnd(boolean isVictory, String username) {
        connectedUsers.get(currentUsername).setStatus(false);
        connectedUsers.get(username).setStatus(false);
        connectedUsers.get(username).getSenderProtocolManager().sendGameEndNotification(isVictory);
        for (var entry : connectedUsers.entrySet()) {
            entry.getValue().getSenderProtocolManager().updateUserList(connectedUsers.entrySet());
        }
    }

    @Override
    public void sendErrorFromClient(String errorMessage) {
        senderProtocolManager.sendError(errorMessage);
    }

    @Override
    public void sendStatus(String username) {
        connectedUsers.get(username).getSenderProtocolManager().updateUserList(connectedUsers.entrySet());
    }

    public class InnerKeepAliveServer extends Thread{
        @Override
        public void run() {
            // synchronized(this) {
            while (keepAliveFlag.get()) {
                try {
                    sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!isStillConnected) quit();
                isStillConnected=false;
            }
        }
    }
    public class InnerKeepAliveClient extends Thread{
        @Override
        public void run() {
                while (keepAliveFlag.get()) {
                    try {
                        sleep(12000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (var user : connectedUsers.values()) {
                        user.getSenderProtocolManager().sendKeepAlive();
                    }
                }
        }
    }
}
