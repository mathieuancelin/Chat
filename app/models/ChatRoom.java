package models;

import api.MessageHandler;
import bots.ImagesBot;
import java.util.*;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class ChatRoom extends Model {

    public static final List<MessageHandler> messagesBots = new ArrayList<MessageHandler>();

    public static final int LASTS  = 100;

    static {
        messagesBots.clear();
        //messagesBots.add(URLBot.INSTANCE);
        messagesBots.add(ImagesBot.INSTANCE);

    }

    public static ChatRoom newRoom(String name, String title) {
        ChatRoom room = new ChatRoom();
        room.title = title;
        room.name = name;
        room.save();
        for (MessageHandler handler : messagesBots) {
           handler.registerChatRoom(name);
        }
        return room;
    }

    @Required 
    public String name;
    
    public String title;
    
    @OneToMany
    public List<Message> messages;
    
    public Boolean closed = new Boolean(false);
    
    public List<Message> join(String user) {
        Message join = new Join(user);
        join.save();
        addMessage(join);
        save();
        return messages;
    }

    public void leave(String user) {
        Message leave = new Leave(user);
        leave.save();
        addMessage(leave);
        save();
    }

    public void say(String user, String text) {
        if(text == null || text.trim().equals("")) {
            return;
        }
        Message mess = new Message(user, text);
        mess.save();
        addMessage(mess);
        save();
    }

    public List<Message> archive() {
        return messages;
    }

    public List<Message> archiveLast100() {
        int start = 0;
        if (messages.size() > LASTS + 1) {
            start = messages.size() - (LASTS + 1);
        }
        int stop = messages.size();
        return messages.subList(start, stop);
    }
    
    private void addMessage(Message message) {
        if (messages == null) {
            messages = new ArrayList<Message>();
        }
        Message m = message;
        for (MessageHandler handler : messagesBots) {
            m = handler.handleChatMessage(m, null, this);
        }
        messages.add(m);

    }
    
    public static ChatRoom get(String n) {
        return ChatRoom.find("byName", n).first();
    }
}

