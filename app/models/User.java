package models;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import play.data.validation.Email;
import play.data.validation.Phone;
import play.data.validation.Required;
import play.data.validation.URL;
import play.db.jpa.Model;
import play.libs.Codec;

@Entity
public class User extends Model {
    
    @Required
    public String username;
    
    @Required
    public String password;
    
    @Required
    public String name;
    
    @Required
    public String surname;
    
    @Phone
    public String phone;
    
    @Required @Email
    public String mail;
    
    public String address;
    
    @URL
    public String avatarUrl;
    
    public String gravatar;
    
    public Boolean connected;
    
    @ManyToOne
    public OrganizationGroup group;

//    public static User findByEmail(@Required @Email String mail) {
//        return User.find("byMail", mail).first();
//    }
    
    public static User createUser(String username, String name, String surname,
            String phone, String mail, String address, String avatar,
            String gravatar, String password, String groupId) {
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
        u = u.save();
        OrganizationGroup group = OrganizationGroup.findByGroupId(groupId);
        group.users.add(u);
        group.save();
        u.group = group;
        return u.save();
    }
    
    public static User findByGroupAndEmail(@Required String groupId, @Required @Email String mail) {
        return User.find("group.groupId = ? and mail = ?", groupId, mail).first();
    }
    
    public static List<User> findByGroupAndConnected(String groupId) {
        return User.find("connected = true and group.groupId = ?", groupId).fetch();
    }
    
    public static User findByGroupAndMailAndPassword(
            String groupId, String mail, String password) {
        return User.find("group.groupId = ? and mail = ? and password = ?", 
                groupId, mail, Codec.hexSHA1(password)).first();
    }
    
    public String emailHash() {
        return Codec.hexSHA1(mail);
    }
    
    @Override
    public String toString() {
        return username + " (" + name + ", " + surname 
                + ", " + mail + ") connected: " + connected; 
    }
    
    public User disconnect() {
        this.connected = false;
        return this.save();
    } 
    
    public User connect() {
        this.connected = true;
        return this.save();
    } 
}
