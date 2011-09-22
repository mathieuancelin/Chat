
import models.OrganizationGroup;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job {

    @Override
    public void doJob() throws Exception {
//        OrganizationGroup.newGroup("serli", "Serli technologies", "SAS").save();
    }
}
