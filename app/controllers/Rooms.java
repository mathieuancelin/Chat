package controllers;

import java.util.ArrayList;
import java.util.List;
import models.ChatRoom;
import models.User;
import play.data.validation.Required;
import play.mvc.*;

public class Rooms extends Controller {
    
    public static final String WELCOME_ROOM = "welcome";
    
    public static final String PRIVATE_KEY = "privroom-";
    
    public static void room(@Required String groupId, @Required String room) {
        if (!session.contains(GroupController.USER_KEY)) {
            flash.error("Please choose a valid user.");
            Application.index();
        }
        User user = User.findByEmail(session.get(GroupController.USER_KEY));
        if (!user.group.groupId.equals(groupId)) {
            room(user.group.groupId, WELCOME_ROOM);
        }
        List events = new ArrayList();
        List<ChatRoom> rooms = ChatRoom.find("priv = false and group.groupId = ?", groupId).fetch();
        List<ChatRoom> privateRooms = ChatRoom
                .find("group.groupId = ? and priv = true and privateUser1 = ? or privateUser2 = ?", 
                groupId, user.mail, user.mail).fetch();
        String roomTitle = "";
        if(!room.equals("welcome")) {
            events = ChatRoom.get(groupId, room).archiveSince(
                    Long.valueOf(session.get(GroupController.FROM_KEY)));
            roomTitle = ChatRoom.get(groupId, room).title;
        }
        List users = User.find("connected = true and group.groupId = ?", groupId).fetch();
        render(user, groupId, users, privateRooms, events, room, rooms, roomTitle);
    }

    public static void say(@Required String groupId, @Required String room, @Required String message) {
        if (!session.contains(GroupController.USER_KEY)) {
            flash.error("Please choose a valid user.");
            Application.index();
        }
        User user = User.findByEmail(session.get(GroupController.USER_KEY));
        ChatRoom.get(groupId, room).say(user, message);
        room(groupId, room);
    }
    
    public static void leaveAllRooms(@Required String groupId) {
        if (!session.contains(GroupController.USER_KEY)) {
            flash.error("Please choose a valid user.");
            Application.index();
        }
        User user = User.findByEmail(session.get(GroupController.USER_KEY));
        for (Object obj : ChatRoom.all().fetch()) {
            ChatRoom chat = ((ChatRoom) obj);
            chat.leave(user);
        }
        user.connected = false;
        user.save();
        session.clear();
        GroupController.index(groupId);
    }
    
    public static void leave(@Required String groupId, String room) {
        if (!session.contains(GroupController.USER_KEY)) {
            flash.error("Please choose a valid user.");
            Application.index();
        }
        User user = User.findByEmail(session.get(GroupController.USER_KEY));
        ChatRoom.get(groupId, room).leave(user);
        session.clear();
        GroupController.index(groupId);
    }
    
    public static void newPrivateRoom(@Required String groupId, @Required String user1, @Required String user2) {
        User u1 = User.findByEmail(user1);
        User u2 = User.findByEmail(user2);
        String value = "Private-" + u1.username + "-" + u2.username;
        ChatRoom room = ChatRoom.newPrivateRoom(groupId, value, 
                "Private conversation between " + value, user1, user2);
        room.save();
        room(groupId, room.name);
    }
    
    public static void newChatRoom(@Required String groupId, @Required String name) {
        System.out.println("newwwww");
        String value = "New room";
        if (name != null && !name.equals("")) {
            value = name;
        }
        ChatRoom room = ChatRoom.newRoom(groupId, value, "Thread title (click to change)");
        room.save();
        ok();
    }
    
    public static void rooms(@Required String groupId) {
        List rooms = ChatRoom.findAll();
        render(rooms);
    }

    public static void setTitle(@Required String groupId, @Required String room, @Required String value) {
        ChatRoom r = ChatRoom.get(groupId, room);
        r.title = value;
        r.save();
        ok();
    }

    public static void getTitle(@Required String groupId, @Required String room) {
        String value = ChatRoom.get(groupId, room).title;
        renderText(value);
    }

}