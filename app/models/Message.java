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
    
    public String username;

    @Lob
    @MaxSize(100000)
    public String text;

    public Long timestamp;
    public MessageType type;
    
    @ManyToOne
    public ChatRoom room;
    
    public Message(String user, String text, MessageType type, ChatRoom room) {
        this.user = user;
        this.text = text.replace("\n", "<br/>").replace("\r", "<br/>");
        this.type = type;
        this.room = room;
        this.username = User.findByGroupAndEmail(room.group.groupId, user).username;
        this.timestamp = System.currentTimeMillis();
    }
    
    public Message(String user, String text, ChatRoom room) {
        this.user = user;
        this.text = text.replace("\n", "<br/>").replace("\r", "<br/>");
        this.type = MessageType.HTML;
        this.room = room;
        this.username = User.findByGroupAndEmail(room.group.groupId, user).username;
        this.timestamp = System.currentTimeMillis();
    }
    
    public static List<Message> findByUser(String user) {
        return Message.find("byUser", user).fetch();
    }
    
    public static List<Message> findByRoomFrom(Long roomId, Long timestamp) {
        return Message.find("room.id = ? and timestamp >= ?", roomId, timestamp).fetch();
    }
    
    public static List<Message> findByRoomFromExcluded(Long roomId, Long timestamp) {
        return Message.find("room.id = ? and timestamp > ?", roomId, timestamp).fetch();
    }
    
    public String username() {
        return username;
    }
}
