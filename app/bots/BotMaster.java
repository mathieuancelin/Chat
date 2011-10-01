package bots;

import api.MessageHandler;
import java.util.ArrayList;
import java.util.List;

public class BotMaster {
    
    public static final List<MessageHandler> messagesBots = new ArrayList<MessageHandler>();

    static {
        messagesBots.clear();
        messagesBots.add(URLBot.INSTANCE);
        messagesBots.add(ImagesBot.INSTANCE);
        messagesBots.add(GistBot.INSTANCE);
    }
    
}
