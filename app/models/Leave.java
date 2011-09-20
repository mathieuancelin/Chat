package models;

import javax.persistence.Entity;

@Entity
public class Leave extends Message {
    
    public Leave(String user) {
        super(user, user + " leave the room.", MessageType.LEAVE);
    }
}

