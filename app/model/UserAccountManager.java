package model;

import controllers.Application;
import play.api.mvc.Result;
import scala.App;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import play.db.*;

import javax.sql.DataSource;

public class UserAccountManager {

    // for offline testing purposes
    private static final boolean usemap = false;
    private static Map<String, Application.UserData> testUsers = new HashMap();

    public static boolean authenticate(Application.Credentials creds) {
        if (usemap) {
            if (userRegistered(creds.email)) return testUsers.get(creds.email).password.equals(creds.password);
            return false;
        }

        return false;
    }

    public static boolean userRegistered(String email) {
        if (usemap) {
            return testUsers.keySet().contains(email);
        }

        /*DataSource ds = DB.getDataSource();

        Connection c = DB.getConnection();
        PreparedStatement stmt = null;
        try {
            String SQL = "";
            stmt = c.prepareStatement(SQL);
            ResultSet rs = stmt.executeQuery();
            stmt.close();
            c.close();
        } catch (SQLException e) {
            System.out.println(e);
        }*/
        return false;
    }

    public static boolean registerUser(Application.UserData data) {
        if (usemap) {
            if (userRegistered(data.email)) return false;
            testUsers.put(data.email, data);
            return true;
        }

        DataSource ds = DB.getDataSource();

        Connection c = DB.getConnection();
        PreparedStatement stmt = null;
        try {
            String SQL = "INSERT INTO users (email,password,name,location,birthday,signupdate) " +
                    "VALUES (" + data.email + "," + data.password + "," + data.location + "," + data.birthday + "," + "3/11/16" + ");";
            stmt = c.prepareStatement(SQL);
            ResultSet rs = stmt.executeQuery();
            stmt.close();
            c.close();
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public static Application.UserData getUser(String email) {
        if (usemap) {
            if (userRegistered(email)) return testUsers.get(email);
            return null;
        }

        return null;
    }
}
