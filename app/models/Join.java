package models;

import javax.persistence.Entity;

@Entity
public class Join extends Message {
    
    public Join(String user) {
        super(user, user + " join the room.", MessageType.JOIN);
    }
}
