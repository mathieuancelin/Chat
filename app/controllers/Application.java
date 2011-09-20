package controllers;

import java.util.ArrayList;
import java.util.List;
import models.ChatRoom;
import models.User;
import play.mvc.*;
import play.data.validation.*;

public class Application extends Controller {

    public static void index() {
        render();
    }
    
    public static void register() {
        render();
    }
    
    public static void registration(String user, String password, String name, 
            String surname, String phone, 
            String mail, String address, 
            String avatar, String gravatar) {
        User u = new User();
        u.username = user;
        u.name = name;
        u.surname = surname;
        u.phone = phone;
        u.mail = mail;
        u.address = address;
        u.avatarUrl = avatar;
        u.gravatar = gravatar;
        u.save();
        index();
    }
    
    public static void enter(@Required String user, @Required String password) {        
        if(validation.hasErrors()) {
            flash.error("Please choose a nick name and the channel.");
            index();
        }    
        User u = User.find("byUsernameAndPassword", user, password).first();
        if (u == null) {
//            if (!u.password.equals(password)) {
//               flash.error("Please choose a valid user.");
//               index(); 
//            }
//        } else {
            flash.error("Please choose a valid user.");
            index();
        }
        for (Object obj : ChatRoom.all().fetch()) {
            ChatRoom chat = ((ChatRoom) obj);
            //if (!chat.closed) {
                chat.join(user);
            //}
        }
        room(user, "welcome");
    }
    
    public static void room(String user, String room) {
        List events = new ArrayList();
        List rooms = ChatRoom.findAll();
        if(!room.equals("welcome")) {
            events = ChatRoom.get(room).messages;
        }
        render(user, events, room, rooms);
    }
    
    public static void say(String user, String room, String message) {
        ChatRoom.get(room).say(user, message);
        room(user, room);
    }
    
    public static void leave(String user, String room) {
        ChatRoom.get(room).leave(user);
        Application.index();
    }
    
    public static void newChatRoom(@Required String name) {
        ChatRoom room = new ChatRoom();
        room.name = name;
        room.save();
        ok();
    }
    
    public static void rooms() {
        List rooms = ChatRoom.findAll();
        render(rooms);
    }
}