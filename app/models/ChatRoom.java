package models;

import api.MessageHandler;
import bots.BotMaster;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class ChatRoom extends Model {

    public static final int LASTS = 20;
    
    public static final String DEFAULT_NAME = "New room";
    
    public static final String DEFAULT_TITLE = "Thread title (click to change)";

    @Required 
    public String name;
    
    public String title;
    
    @OneToMany
    public List<Message> messages;
    
    @ManyToOne
    public OrganizationGroup group;
    
    public Boolean closed = new Boolean(false);
    
    @Required
    public Boolean priv;
    
    public String privateUser1;
    
    public String privateUser2;
        
    @ManyToMany
    public List<User> connectedUsers;

    public String privateUser1() {
        User u = User.findByGroupAndEmail(group.groupId, privateUser1);
        if (u != null) {
            return u.username;
        } else {
            return "unknown (error)";
        }
    }
    
    public String privateUser2() {
        User u = User.findByGroupAndEmail(group.groupId, privateUser2);
        if (u != null) {
            return u.username;
        } else {
            return "unknown (error)";
        }
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (priv) {
            builder.append("[PRIV] ");
        } else {
            builder.append("[PUB] ");
        }
        if (group != null) {
            builder.append(" ").append(group.groupId).append(" ");
        } else {
            builder.append(" no group ");
        }
        builder.append(" : ").append(name);
        return builder.toString();
    }
    
    public List<Message> join(User user) {
        Message join = new Join(user.mail, this);
        join.save();
        addMessage(join);
        connectedUsers.add(user);
        save();
        return messages;
    }

    public void leave(User user) {
        Message leave = new Leave(user.mail, this);
        leave.save();
        addMessage(leave);
        connectedUsers.remove(user);
        save();
    }

    public void say(User user, String text) {
        if(text == null || text.trim().equals("")) {
            return;
        }
        Message mess = new Message(user.mail, text, this);
        mess.save();
        addMessage(mess);
        save();
    }

    public List<Message> archive() {
        return messages;
    }

    public List<Message> archiveSince(Long timestamp) {
        return Message.findByRoomFrom(id, timestamp);
    }
    
    public List<Message> archiveSinceExcluded(Long timestamp) {
        return Message.findByRoomFromExcluded(id, timestamp);
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
        for (MessageHandler handler : BotMaster.messagesBots) {
            m = handler.handleChatMessage(m, null, this);
        }
        messages.add(m);

    }
    
    public static ChatRoom getOrCreateRoom(
            String groupId, String name, String title) {
        ChatRoom room = ChatRoom.findByGroupAndName(groupId, name);
        if ( room != null) {
            return room;
        }
        room = createRoom(title, name, false, null, null, groupId);
        for (MessageHandler handler : BotMaster.messagesBots) {
           handler.registerChatRoom(name);
        }
        return room;
    }
    
    public static ChatRoom getOrCreatePrivateRoom(
            String groupId, String name, 
            String title, String user1, String user2) {
        ChatRoom room1 = ChatRoom.
                findByPrivate1AndPrivate2AndGroup(user1, user2, groupId);
        ChatRoom room2 = ChatRoom.
                findByPrivate1AndPrivate2AndGroup(user2, user1, groupId);
        if ( room1 != null) {
            return room1;
        }
        if ( room2 != null) {
            return room2;
        }
        ChatRoom room = createRoom(title, name, true, user1, user2, groupId);
        for (MessageHandler handler : BotMaster.messagesBots) {
           handler.registerChatRoom(name);
        }
        return room;
    }
    
    private static ChatRoom createRoom(
            String title, String name, boolean priv, 
            String user1, String user2, String groupId) {
        ChatRoom room = new ChatRoom();
        room.title = title;
        room.name = name;
        room.priv = priv;
        room.privateUser1 = user1;
        room.privateUser2 = user2;
        room.connectedUsers = new ArrayList<User>();
        room = room.save();
        OrganizationGroup group = OrganizationGroup.findByGroupId(groupId);
        group.rooms.add(room);
        group.save();
        room.group = group;        
        return room.save();
    }
    
    public static ChatRoom findByPrivate1AndPrivate2AndGroup(
            String user1, String user2, String groupId) {
        return ChatRoom.find("privateUser1 = ? and privateUser2 = ? "
            + "and group.groupId = ?", user1, user2, groupId).first();
    }
    
    public static ChatRoom findByGroupAndName(String groupId, String n) {
        return ChatRoom.find("name = ? and closed = false and "
            + "group.groupId = ?", n, groupId).first();
    }
    
    public static List<ChatRoom> findPublicRoomsByGroup(String groupId) {
        return ChatRoom.find("priv = false and closed = false and group.groupId = ?", 
                groupId).fetch();
    }
    
    public static List<ChatRoom> findPrivateRoomsByGroupAndUser(
            String groupId, User user) {
        return ChatRoom
            .find("group.groupId = ? and priv = true "
            + "and privateUser1 = ? or privateUser2 = ?", 
            groupId, user.mail, user.mail).fetch();
    }
}

