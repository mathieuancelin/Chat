package models;

import javax.persistence.Entity;

@Entity
public class Leave extends Message {
    
    public Leave(String user, ChatRoom room) {
        super(user, user + " left the room.", MessageType.LEAVE, room);
    }
}

