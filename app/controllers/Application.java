package controllers;

import java.io.File;
import java.util.List;
import models.OrganizationGroup;
import play.cache.Cache;
import play.libs.Files;
import play.libs.Images;
import play.mvc.*;

public class Application extends Controller {
    
    public static final File uploads = new File("uploads");

    public static void index() {
        List groups = OrganizationGroup.findAll();
        render(groups);
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