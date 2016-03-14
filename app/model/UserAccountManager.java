package model;

import controllers.Application;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import play.db.*;

import javax.sql.DataSource;

public class UserAccountManager {

    // for offline testing purposes
    private static final boolean noDatabase = false;
    private static Map<String, Application.UserData> testUsers = new HashMap<>();
    private static ArrayList<LogEntry> log = new ArrayList<>();

    public static boolean authenticate(Application.Credentials creds) {
        if (noDatabase) {
            if (userRegistered(creds.email)) return testUsers.get(creds.email).password.equals(creds.password);
            return false;
        }

        Application.UserData user = getUser(creds.email);
        return (user != null && user.email.equals(creds.email) && user.password.equals(creds.password));
    }

    public static boolean isAdmin(String email) {
        return (email.equals("dylhunn@gmail.com"));
    }

    public static boolean userRegistered(String email) {
        if (noDatabase) {
            return testUsers.keySet().contains(email);
        }

        Application.UserData user = getUser(email);
        return (user != null);
    }

    public static boolean registerUser(Application.UserData data) {
        if (noDatabase) {
            if (userRegistered(data.email)) return false;
            testUsers.put(data.email, data);
            return true;
        }

        DataSource ds = DB.getDataSource();
        Connection c = DB.getConnection();
        boolean success = false;
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("INSERT INTO users (email,password,name,location,birthday,signupdate, verified) " +
                    "VALUES (?,?,?,?,?,?, ?);");

            String currDate = LocalDateTime.now(ZoneId.of("America/Los_Angeles")).toString();

            stmt.setString(1,data.email);
            stmt.setString(2,data.password);
            stmt.setString(3,data.name);
            stmt.setString(4,data.location);
            stmt.setString(5,data.birthday);
            stmt.setString(6,currDate);
            stmt.setString(7,"false");

            success = stmt.execute();
            stmt.close();
            c.close();
        } catch (SQLException e) {
            writeLog(data.email, "registerUser", data.toString(), e.getMessage(), false);
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (c != null) c.close();
            } catch (Exception e) {
                writeLog(data.email, "registerUser", data.toString(), e.getMessage(), false);
            }
        }
        return success;
    }

    public static Application.UserData getUser(String email) {
        if (noDatabase) {
            if (userRegistered(email)) return testUsers.get(email);
            return null;
        }

        Application.UserData result = new Application.UserData();

        DataSource ds = DB.getDataSource();
        Connection c = DB.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String SQL = "SELECT email, password, name, location, birthday, signupdate FROM users WHERE email = ?;";
            stmt = c.prepareStatement(SQL);
            stmt.setString(1, email);
            rs = stmt.executeQuery();

            if (!rs.next()) return null; // the cursor is moved
            result.email = rs.getString(1);
            result.password = rs.getString(2);
            result.name = rs.getString(3);
            result.location = rs.getString(4);
            result.birthday = rs.getString(5);
            result.signupdate = rs.getString(6);


        } catch (SQLException e) {
            writeLog(email, "getUser", "", e.getMessage(), false);
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (c != null) c.close();
            } catch (Exception e) {
                writeLog(email, "getUser", "", e.getMessage(), false);
            }
        }

        return result;
    }

    public static boolean writeLog(String email, String type, String request, String result, Boolean success) {
        String currDate = LocalDateTime.now(ZoneId.of("America/Los_Angeles")).toString();

        if (noDatabase) { // no logging offline yet
            log.add(new LogEntry(email, type, request, currDate, result, success.toString()));
            return true;
        }
        boolean reqsuccess = false;

        DataSource ds = DB.getDataSource();
        Connection c = DB.getConnection();
        String SQL = "INSERT INTO logs (email,type,request,datetime,result,success) " +
                "VALUES (?, ?, ?, ?, ?, ?);";
        PreparedStatement stmt = null;

        try {
            stmt = c.prepareStatement(SQL);
            stmt.setString(1, email);
            stmt.setString(2, type);
            stmt.setString(3, request);
            stmt.setString(4, currDate);
            stmt.setString(5, result);
            stmt.setString(6, success.toString());
            reqsuccess = stmt.execute();
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (c != null) c.close();
             } catch (Exception e) {
                System.out.println(e);
            }
         }
        return reqsuccess;
    }

    public static List<LogEntry> getLogs() {
        if (noDatabase) {
            return log;
        }
        List<LogEntry> res = new ArrayList<>();

        DataSource ds = DB.getDataSource();
        Connection c = DB.getConnection();
        String SQL = "SELECT * FROM logs";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = c.prepareStatement(SQL);
            rs = stmt.executeQuery();
            while (rs.next()) {
                res.add(new LogEntry(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)));
            }
        } catch (SQLException e) {
            return res;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (c != null) c.close();
            } catch (Exception e) {
                return res;
            }
        }
        return res;
    }

    public static class LogEntry {
        public LogEntry(String email, String type, String request, String currDate, String result, String success) {
            this.email=email;
            this.type=type;
            this.request=request;
            this.currDate=currDate;
            this.result=result;
            this.success=success;
        }
        public String email;
        public String type;
        public String request;
        public String currDate;
        public String result;
        public String success;
    }
}
