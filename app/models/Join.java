package models;

import javax.persistence.Entity;

@Entity
public class Join extends Message {
    
    public Join(String user, ChatRoom room) {
        super(user, user + " joined the room.", MessageType.JOIN, room);
    }
}
