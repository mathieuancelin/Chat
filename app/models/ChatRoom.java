package models;

import java.util.*;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.libs.F.*;

@Entity
public class ChatRoom extends Model {
    
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
    
    private void addMessage(Message message) {
        if (messages == null) {
            messages = new ArrayList<Message>();
        }
        messages.add(message);
    }
    
    public static ChatRoom get(String n) {
        return ChatRoom.find("byName", n).first();
    }
}

