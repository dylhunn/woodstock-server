package model;

import controllers.Application;
import play.api.mvc.Result;
import scala.App;

import java.sql.*;
import java.time.LocalDateTime;
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

        Application.UserData user = getUser(creds.email);
        return (user != null && user.email == creds.email && user.password == creds.password);
    }

    public static boolean userRegistered(String email) {
        if (usemap) {
            return testUsers.keySet().contains(email);
        }

        Application.UserData user = getUser(email);
        return (user != null);
    }

    public static boolean registerUser(Application.UserData data) {
        if (usemap) {
            if (userRegistered(data.email)) return false;
            testUsers.put(data.email, data);;
            return true;
        }

        DataSource ds = DB.getDataSource();
        Connection c = DB.getConnection();
        PreparedStatement stmt = null;
        try {
            String currDate = LocalDateTime.now().toLocalDate().toString();
            String SQL = "INSERT INTO users (email,password,name,location,birthday,signupdate) " +
                    "VALUES ('" + data.email + "','" + data.password + "','" + data.name + "','" + data.location + "','"
                    + data.birthday + "','" + currDate + "');";
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

        Application.UserData result = null;

        DataSource ds = DB.getDataSource();
        Connection c = DB.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String SQL = "SELECT email, password, name, location, birthday, signupdate FROM users WHERE email='" + email + "';";
            stmt = c.createStatement();
            rs = stmt.executeQuery(SQL);

            if (!rs.next()) return null; // the cursor is moved
            result.email = rs.getString(1);
            result.password = rs.getString(2);
            result.name = rs.getString(3);
            result.location = rs.getString(4);
            result.birthday = rs.getString(5);
            result.signupdate = rs.getString(6);

            rs.close();
            stmt.close();
            c.close();
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }

        return result;
    }
}
