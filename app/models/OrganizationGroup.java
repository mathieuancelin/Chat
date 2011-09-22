package models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class OrganizationGroup extends Model {
    
    @Required
    public String groupId;
    
    @Required
    public String name;
    
    @Lob
    @MaxSize(100000)
    public String desc;
    
    @OneToMany
    public List<User> users;
    
    @OneToMany
    public List<ChatRoom> rooms;
    
    public OrganizationGroup() {}
    
    public static OrganizationGroup newGroup(String groupId, String name, String description) {
        OrganizationGroup group = new OrganizationGroup();
        group.name = name;
        group.groupId = groupId;
        group.desc = description;
        group.users = new ArrayList<User>();
        group.rooms = new ArrayList<ChatRoom>();
        return group;
    }
    
    public static OrganizationGroup newGroup(String groupId, String name) {
        return newGroup(groupId, name, "");
    }
  
    public static OrganizationGroup findByGroupId(String id) {
        return OrganizationGroup.find("byGroupId", id).first();
    }
    
    @Override
    public String toString() {
        return groupId  + " - " + name;
    }
}
