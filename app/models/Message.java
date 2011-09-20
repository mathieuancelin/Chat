package models;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import net.sf.oval.constraint.MaxSize;
import play.db.jpa.Model;

@Entity
public class Message extends Model {

    public String user;

    @Lob
    @MaxSize(100000)
    public String text;

    public Long timestamp;
    public MessageType type;
    
    @ManyToOne
    public ChatRoom room;

    public Message(String user, String text, MessageType type) {
        this.user = user;
        this.text = text.replace("\n", "<br/>").replace("\r", "<br/>");
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }
    
    public Message(String user, String text) {
        this.user = user;
        this.text = text.replace("\n", "<br/>").replace("\r", "<br/>");
        this.type = MessageType.HTML;
        this.timestamp = System.currentTimeMillis();
    }
    
    public static List<Message> forUser(String user) {
        return Message.find("byUser", user).fetch();
    }
    
    
}
