package models;

import javax.persistence.Entity;
import play.data.validation.Email;
import play.data.validation.Phone;
import play.data.validation.Required;
import play.data.validation.URL;
import play.db.jpa.Model;

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
    
}
