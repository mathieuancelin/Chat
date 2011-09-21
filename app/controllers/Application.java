package controllers;

import java.io.File;
import models.ChatRoom;
import models.User;
import play.cache.Cache;
import play.data.validation.Email;
import play.data.validation.Required;
import play.data.validation.URL;
import play.mvc.*;
import play.libs.Codec;
import play.libs.Files;
import play.libs.Images;

public class Application extends Controller {

    public static final File uploads = new File("uploads");

    public static final String USER_KEY = "auth-user";
    
    public static final String FROM_KEY = "from-time";
    
    public static final String WELCOME_ROOM = "welcome";
    
    public static final String PRIVATE_KEY = "privroom-";

    public static void index() {
        boolean logged = false;
        User user = null;
        if (session.contains(USER_KEY)) {
            logged = true;
            user = User.findByEmail(session.get(USER_KEY));
            if (user == null) {
                logged = false;
                session.clear();
            }
        }
        render(user, logged);
    }

    public static void signin() {
        session.put(FROM_KEY, System.currentTimeMillis());
        boolean logged = false;
        if (session.contains(USER_KEY)) {
            logged = true;
        }
        if (logged) {
            User user = User.findByEmail(session.get(USER_KEY));
            Rooms.room(WELCOME_ROOM);
        }
        render(logged);
    }
    
    public static void register() {
        String randomID = Codec.UUID();
        String username = "";
        String password = "";
        String name = "";
        String surname = "";
        String mail = "";
        String avatar  ="";
        render(randomID, username, password, name, surname, mail, avatar);
    }
    
    public static void registration(
            @Required String username, 
            @Required String password, 
            @Required String repassword, 
            @Required String name, 
            @Required String surname, 
            String phone, 
            @Required @Email String mail, 
            String address, 
            @URL String avatar, 
            @URL String gravatar, 
            @Required String code, 
            @Required String randomID) {
        if (!password.equals(repassword)) {
            flash.error("Passwords don't match !");
            randomID = Codec.UUID();
            render("Application/register.html", randomID, username, 
                    password, name, surname, mail, avatar);
        }
        if (!validation.equals(code, Cache.get(randomID)).ok) {
            flash.error("Wrong CAPTCHA !!!");
            randomID = Codec.UUID();
            render("Application/register.html", randomID, username, 
                    password, name, surname, mail, avatar);
        }
        if(validation.hasErrors()) {
            flash.error(validation.errorsMap().toString());
            signin();
        }
        User existing = User.find("byMail", mail).first();
        if (existing != null) {
            flash.error("Email address already exists");
            randomID = Codec.UUID();
            render("Application/register.html", randomID, username, 
                    password, name, surname, mail, avatar);
        }
        User u = new User();
        u.username = username;
        u.name = name;
        u.surname = surname;
        u.phone = phone;
        u.mail = mail;
        u.address = address;
        u.avatarUrl = avatar;
        u.gravatar = gravatar;
        u.password = Codec.hexSHA1(password);
        u.connected = false;
        u.save();
        signin();
    }
    
    public static void enter(@Required @Email String user, @Required String password) {        
        if(validation.hasErrors()) {
            flash.error("Please choose a nick name and the channel.");
            index();
        }
        User u = User.find("byMailLikeAndPassword", user, Codec.hexSHA1(password)).first();
        if (u == null) {
            flash.error("Please choose a valid user.");
            index();
        }
        session.put(USER_KEY, u.mail);
        for (Object obj : ChatRoom.all().fetch()) {
            ChatRoom chat = ((ChatRoom) obj);
            if (!chat.closed) {
                chat.join(u);
            }
        }
        u.connected = true;
        u.save();
        Rooms.room(WELCOME_ROOM);
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