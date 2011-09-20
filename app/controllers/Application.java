package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import models.ChatRoom;
import models.User;
import play.cache.Cache;
import play.mvc.*;
import play.data.validation.*;
import play.libs.Codec;
import play.libs.Files;
import play.libs.Images;

public class Application extends Controller {

    public static final File uploads = new File("uploads");

    public static final String USER_KEY = "auth-user";

    public static void index() {
        boolean logged = false;
        if (session.contains(USER_KEY)) {
            logged = true;
        }
        render(logged);
    }

    public static void signin() {
        boolean logged = false;
        if (session.contains(USER_KEY)) {
            logged = true;
        }
        
        render();
    }
    
    public static void register() {
        String randomID = Codec.UUID();
        render(randomID);
    }
    
    public static void registration(String user, String password, String name, 
            String surname, String phone, 
            String mail, String address, 
            String avatar, String gravatar, 
            @Required(message="Please type the code") String code, String randomID) {
        validation.equals(code, Cache.get(randomID))
                .message("Invalid code. Please type it again");
        if(validation.hasErrors()) {
            flash.error("Wrong CAPTCHA !!!");
            signin();
        }
        User u = new User();
        u.username = user;
        u.name = name;
        u.surname = surname;
        u.phone = phone;
        u.mail = mail;
        u.address = address;
        u.avatarUrl = avatar;
        u.gravatar = gravatar;
        u.password = password;
        u.save();
        signin();
    }
    
    public static void enter(@Required String user, @Required String password) {        
        if(validation.hasErrors()) {
            flash.error("Please choose a nick name and the channel.");
            signin();
        }
        User u = User.find("byMailLikeAndPassword", user, password).first();
        if (u == null) {
            flash.error("Please choose a valid user.");
            signin();
        }
        session.put(USER_KEY, u.mail);
        for (Object obj : ChatRoom.all().fetch()) {
            ChatRoom chat = ((ChatRoom) obj);
            //if (!chat.closed) {
                chat.join(user);
            //}
        }
        room("welcome");
    }
    
    public static void room(@Required String room) {
        if (!session.contains(USER_KEY)) {
            flash.error("Please choose a valid user.");
            signin();
        }
        User user = User.fromEmail(session.get(USER_KEY));
        List events = new ArrayList();
        List rooms = ChatRoom.findAll();
        String roomTitle = "";
        if(!room.equals("welcome")) {
            events = ChatRoom.get(room).archiveLast100();
            roomTitle = ChatRoom.get(room).title;
        }
        render(user, events, room, rooms, roomTitle);
    }
    
    public static void say(@Required String room, @Required String message) {
        if (!session.contains(USER_KEY)) {
            flash.error("Please choose a valid user.");
            signin();
        }
        User user = User.fromEmail(session.get(USER_KEY));
        ChatRoom.get(room).say(user.name, message);
        room(room);
    }
    
    public static void leave(String room) {
        if (!session.contains(USER_KEY)) {
            flash.error("Please choose a valid user.");
            signin();
        }
        User user = User.fromEmail(session.get(USER_KEY));
        ChatRoom.get(room).leave(user.name);
        session.clear();
        Application.signin();
    }
    
    public static void newChatRoom(@Required String name) {
        ChatRoom room = ChatRoom.newRoom(name, "New Thread ...");
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

    public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha();
        String code = captcha.getText();
        Cache.set(id, code, "10mn");
        renderBinary(captcha);
    }

    public static void upload(File file) {
        System.out.println("upload " + file.getName());
        if (!uploads.exists()) {
            uploads.mkdirs();
        }
        Files.copy(file, new File(uploads, file.getName()));
    }
}