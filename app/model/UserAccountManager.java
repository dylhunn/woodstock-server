package model;

import controllers.Application;
import scala.App;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import play.db.*;

import javax.sql.DataSource;

public class UserAccountManager {

    private static final boolean usemap = true;
    private static Map<String, Application.UserData> testUsers = new HashMap();

    public static boolean authenticate(Application.Credentials creds) {
        if (usemap) if (userRegistered(creds.email)) return testUsers.get(creds.email).password.equals(creds.password);
        if (usemap) return false;

        return false;
    }

    public static boolean userRegistered(String email) {
        DataSource ds = DB.getDataSource();

        Connection c = DB.getConnection();
        //try {
        //    Statement stmt = c.createStatement();
        //    ResultSet rs = stmt.executeQuery("SELECT a, b, c FROM Table1");
        //    c.close();
        //} catch (Exception e) {
            // TODO
          //  System.out.println(e.toString());
        //}
        if (usemap) return testUsers.keySet().contains(email);
        if (usemap) return false;

        return false;
    }

    public static boolean registerUser(Application.UserData data) {
        if (usemap) if (userRegistered(data.email)) return false;
        testUsers.put(data.email, data);
        if (usemap) return true;

        return true;
    }

    public static Application.UserData getUser(String email) {
        if (usemap) if (userRegistered(email)) return testUsers.get(email);
        return null;
    }
}
