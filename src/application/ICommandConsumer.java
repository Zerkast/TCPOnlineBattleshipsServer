package application;
public interface ICommandConsumer {
    public void join(String username);
    public void quit();
    public void notifyGameStart(String username);
    public void sendInvite(String username);
    public void sendErrorFromClient(String errorMessage);
    public void sendStatus(String username);
    public void sendInviteResponse(String username, boolean hasAccepted);
    public void notifyGameReady(String username); //IL CLIENT SI MEMORIZZA IL NOME DELL'ALTRO GIOCATORE CON CUI STA GIOCANDO
    public void shoot(int posX, int posY, String username);
    public void shootOutcome(boolean hasHit, String username);
    public void setKeepAlive();
    public void notifyGameEnd(boolean isVictory, String username);
}
