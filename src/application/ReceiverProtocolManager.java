package application;
import java.text.ParseException;

import net.IMessageConsumer;

public class ReceiverProtocolManager implements IMessageConsumer{
    private ICommandConsumer consumer;
    private SenderProtocolManager senderProtocolManager;

    public ReceiverProtocolManager(ICommandConsumer consumer, SenderProtocolManager spm) {
        this.consumer = consumer;
        senderProtocolManager = spm;
    }


    public void consumeMessage(String message) {
            System.out.println(message);
            String[] comando = message.split("#");
            if (comando.length==0)  {
                senderProtocolManager.sendError(0); 
            } else {
                String[] argomenti = new String[0];
                if (comando.length>1) argomenti = comando[1].split("&");
                    switch (comando[0]) {
                        case "quit":
                            consumer.quit();
                            break;
                        case "join": 
                            consumer.join(argomenti[0]);
                            break;
                        case "gameStart":
                            if (argomenti.length==0) senderProtocolManager.sendError(1);
                            else consumer.notifyGameStart(argomenti[0]);
                            break;
                        case "gameReady":
                            if (argomenti.length==0) senderProtocolManager.sendError(1);
                            consumer.notifyGameReady(argomenti[0]);
                            break;
                        case "invite":
                            if (argomenti.length==0) senderProtocolManager.sendError(1);
                            else consumer.sendInvite(argomenti[0]);
                            break;
                        case "inviteResponse":
                            if (argomenti.length<2) senderProtocolManager.sendError(1);
                            else {
                                try {
                                    consumer.sendInviteResponse(argomenti[0], parseBool(argomenti[1]));
                                } catch (ParseException e) {
                                    senderProtocolManager.sendError(1);
                                }
                            }
                            break;
                        case "shootResponse":
                            if (argomenti.length<2) senderProtocolManager.sendError(1);
                            else {
                                try {
                                    consumer.shootOutcome(parseBool(argomenti[0]), argomenti[1]);
                                } catch (ParseException e) {
                                    senderProtocolManager.sendError(1);
                                }
                            }
                            break;
                        case "hi":
                            consumer.setKeepAlive();
                            break;
                        case "shoot":
                            if (argomenti.length<3) senderProtocolManager.sendError(1);
                            else {
                                try {
                                    consumer.shoot(Integer.parseInt(argomenti[0]), Integer.parseInt(argomenti[1]), argomenti[2]);
                                } catch (NumberFormatException e) {
                                    senderProtocolManager.sendError(1); 
                                }
                            }
                            break;
                        case "gameEnd":
                            if (argomenti.length<2) senderProtocolManager.sendError(1);
                            try {
                                consumer.notifyGameEnd(parseBool(argomenti[1]), argomenti[0]);    
                            } catch (ParseException e) {
                                senderProtocolManager.sendError(1);
                            }     
                            break;
                        case "error":
                            if (argomenti.length==0) senderProtocolManager.sendError(1);
                            consumer.sendErrorFromClient(message);
                            break;
                        case "checkUsers":
                            if (argomenti.length==0) senderProtocolManager.sendError(1);
                            consumer.sendStatus(argomenti[0]);
                            break;
                        default:
                            senderProtocolManager.sendError(0);
                    }
            }
    }


    private boolean parseBool(String s) throws ParseException{ //ho fatto questo metodo perche non mi piace che parseBoolean restituisca false anche quando gli arriva una stringa invalida diversa da "false"
        if (s.equals("t")) return true;
        if (s.equals("f")) return false;
        else throw new ParseException("Invalid input", 0);
    }
}
