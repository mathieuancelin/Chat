package controllers;

public class Security extends Secure.Security {
	
    static boolean authenticate(String username, String password) {
        if (username.equals("admin") && password.equals("admin")) {
            return true;
        }
        return false;
    }
}
