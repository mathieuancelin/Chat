package controllers;

import java.util.ArrayList;
import java.util.List;
import models.ChatRoom;
import models.User;
import play.data.validation.Required;
import play.mvc.*;

public class Rooms extends Controller {
    
    public static void room(@Required String room) {
        if (!session.contains(Application.USER_KEY)) {
            flash.error("Please choose a valid user.");
            Application.index();
        }
        User user = User.findByEmail(session.get(Application.USER_KEY));
        List events = new ArrayList();
        List<ChatRoom> rooms = ChatRoom.find("byPriv", false).fetch();
        List<ChatRoom> privateRooms = ChatRoom
                .find("priv = true and privateUser1 = ? or privateUser2 = ?", 
                user.mail, user.mail).fetch();
        String roomTitle = "";
        if(!room.equals("welcome")) {
            events = ChatRoom.get(room).archiveSince(
                    Long.valueOf(session.get(Application.FROM_KEY)));
            roomTitle = ChatRoom.get(room).title;
        }
        List users = User.find("byConnected", true).fetch();
        render(user, users, privateRooms, events, room, rooms, roomTitle);
    }

    public static void say(@Required String room, @Required String message) {
        if (!session.contains(Application.USER_KEY)) {
            flash.error("Please choose a valid user.");
            Application.index();
        }
        User user = User.findByEmail(session.get(Application.USER_KEY));
        ChatRoom.get(room).say(user, message);
        room(room);
    }
    
    public static void leaveAllRooms() {
        if (!session.contains(Application.USER_KEY)) {
            flash.error("Please choose a valid user.");
            Application.index();
        }
        User user = User.findByEmail(session.get(Application.USER_KEY));
        for (Object obj : ChatRoom.all().fetch()) {
            ChatRoom chat = ((ChatRoom) obj);
            chat.leave(user);
        }
        user.connected = false;
        user.save();
        session.clear();
        Application.index();
    }
    
    public static void leave(String room) {
        if (!session.contains(Application.USER_KEY)) {
            flash.error("Please choose a valid user.");
            Application.index();
        }
        User user = User.findByEmail(session.get(Application.USER_KEY));
        ChatRoom.get(room).leave(user);
        session.clear();
        Application.index();
    }
    
    public static void newPrivateRoom(@Required String user1, @Required String user2) {
        User u1 = User.findByEmail(user1);
        User u2 = User.findByEmail(user2);
        String value = "Private-" + u1.username + "-" + u2.username;
        ChatRoom room = ChatRoom.newPrivateRoom(value, 
                "Private conversation between " + value, user1, user2);
        room.save();
        room(room.name);
    }
    
    public static void newChatRoom(@Required String name) {
        String value = "New room";
        if (name != null && !name.equals("")) {
            value = name;
        }
        ChatRoom room = ChatRoom.newRoom(value, "Thread title (click to change)");
        room.save();
        ok();
    }
    
    public static void rooms() {
        List rooms = ChatRoom.findAll();
        render(rooms);
    }

    public static void setTitle(@Required String room, @Required String value) {
        ChatRoom r = ChatRoom.get(room);
        r.title = value;
        r.save();
        ok();
    }

    public static void getTitle(@Required String room) {
        String value = ChatRoom.get(room).title;
        renderText(value);
    }

}