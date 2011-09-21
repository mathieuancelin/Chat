package models;

import api.MessageHandler;
import bots.ImagesBot;
import bots.URLBot;
import java.util.*;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class ChatRoom extends Model {

    public static final List<MessageHandler> messagesBots = new ArrayList<MessageHandler>();

    public static final int LASTS = 20;

    static {
        messagesBots.clear();
        messagesBots.add(URLBot.INSTANCE);
        messagesBots.add(ImagesBot.INSTANCE);
    }

    public static ChatRoom newRoom(String name, String title) {
        ChatRoom room = ChatRoom.get(name);
        if ( room != null) {
            return room;
        }
        room = new ChatRoom();
        room.title = title;
        room.name = name;
        room.priv = false;
        room.save();
        for (MessageHandler handler : messagesBots) {
           handler.registerChatRoom(name);
        }
        return room;
    }
    
    public static ChatRoom newPrivateRoom(String name, String title, String user1, String user2) {
        ChatRoom room1 = ChatRoom.find("privateUser1 = ? and privateUser2 = ?", user1, user2).first();
        ChatRoom room2 = ChatRoom.find("privateUser1 = ? and privateUser2 = ?", user2, user1).first();
        if ( room1 != null) {
            return room1;
        }
        if ( room2 != null) {
            return room2;
        }
        ChatRoom room = new ChatRoom();
        room.title = title;
        room.name = name;
        room.priv = true;
        room.privateUser1 = user1;
        room.privateUser2 = user2;
        room.save();
        for (MessageHandler handler : messagesBots) {
           handler.registerChatRoom(name);
        }
        return room;
    }
    
    public Boolean priv;
    
    public String privateUser1;
    
    public String privateUser2;
    
    public String privateUser1() {
        return User.findByEmail(privateUser1).username;
    }
    
    public String privateUser2() {
        return User.findByEmail(privateUser2).username;
    }

    @Required 
    public String name;
    
    public String title;
    
    @OneToMany
    public List<Message> messages;
    
    public Boolean closed = new Boolean(false);
    
    public List<Message> join(User user) {
        Message join = new Join(user.mail);
        join.save();
        addMessage(join);
        save();
        return messages;
    }

    public void leave(User user) {
        Message leave = new Leave(user.mail);
        leave.save();
        addMessage(leave);
        save();
    }

    public void say(User user, String text) {
        if(text == null || text.trim().equals("")) {
            return;
        }
        Message mess = new Message(user.mail, text);
        mess.room = this;
        mess.save();
        addMessage(mess);
        save();
    }

    public List<Message> archive() {
        return messages;
    }

    public List<Message> archiveSince(Long timestamp) {
        return Message.find("room.id = ? and timestamp >= ?", this.id, timestamp).fetch();
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

