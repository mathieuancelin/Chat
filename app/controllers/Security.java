package controllers;

import play.Play;
import play.libs.Codec;

public class Security extends Secure.Security {
	
    static boolean authenticate(String username, String password) {
        String adminPassword = Play.configuration.getProperty("admin-password");
        String hashedPassword = Codec.hexSHA1(password);
        if (username.equals("admin") && hashedPassword.equals(adminPassword)) {
            return true;
        }
        return false;
    }
}
