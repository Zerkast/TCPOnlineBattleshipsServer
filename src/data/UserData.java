package data;

import application.SenderProtocolManager;

public class UserData {
    private SenderProtocolManager spm;
    private boolean status;
   
    public UserData(SenderProtocolManager spm) {
        this.spm = spm;
    }

    public SenderProtocolManager getSenderProtocolManager() {
        return spm;
    }

    public void setSenderProtocolManager(SenderProtocolManager spm) {
        this.spm = spm;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
