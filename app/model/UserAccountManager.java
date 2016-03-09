package model;

import controllers.Application;
import scala.App;

import java.util.HashMap;
import java.util.Map;

public class UserAccountManager {

    private static Map<String, Application.UserData> users = new HashMap();

    public static boolean authenticate(Application.Credentials creds) {
        if (userRegistered(creds.email)) return users.get(creds.email).password.equals(creds.password);
        return false;
    }

    public static boolean userRegistered(String email) {
        return users.keySet().contains(email);
    }

    public static boolean registerUser(Application.UserData data) {
        if (userRegistered(data.email)) return false;
        users.put(data.email, data);
        return true;
    }

    public static Application.UserData getUser(String email) {
        if (userRegistered(email)) return users.get(email);
        return null;
    }
}
