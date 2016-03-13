package controllers;

import model.UserAccountManager;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import harmony.core.Harmonizer;

import play.twirl.api.Content;
import views.html.*;

import java.net.URLDecoder;
import java.util.*;

import static play.data.Form.form;

public class Application extends Controller {

    // SITE NAVIGATION METHODS

    public Result index() {
        if (validSessionIsActive()) return harmonizepage();
        else return landing();
    }

    public Result about() {
        String title = "About Woodstock";
        String content = "Woodstock transforms chord symbols into music. " +
                "Specifically, the goal of the project to to generate four-part chorales that are compliant with the traditional " +
                "rules of common practice period harmony. This project is still pre-alpha." +
                "<p>The following components are used under the MIT license: " +
                "<ul><li>Music rendered in the browser with VexFlow</li>" +
                "<li>Audio generated with Tone.js</li>" +
                "<li>Bootstrap framework</li>" +
                "<li>Bootswatch Cosmo theme</li></ul>" +
                "<p>These components are used under the Apache 2 License:" +
                "<ul><li>Play Framework 2</li></ul>";
        Content html = views.html.auxtemplate.render(title, content);
        return ok(html);
    }

    public Result contact() {
        String title = "Contact";
        String content = "This project is maintained by Dylan D. Hunn. You can reach him at dylhunn [at] gmail [dot] com.";
        Content html = views.html.auxtemplate.render(title, content);
        return ok(html);
    }

    public Result landing() {
        return ok(views.html.landing.render(form(Credentials.class)));
    }

    public Result login(String message) {
        if (validSessionIsActive()) return index();
        if (message == null) message = "";
        return ok(views.html.loginpage.render(Form.form(Credentials.class), message));
    }

    public Result logout() {
        String user = session("user-email");
        session().clear();
        if (user == null) return login("You were not logged in.");
        return login("User " + user + " has been logged out.");
    }

    public Result harmonizepage() {
        if (!validSessionIsActive()) return login("Please sign in to acess this page.");
        return ok(views.html.harmonize.render());
    }

    public Result testRegister() {
        return ok();//views.html.signuppage.render(Form.form(UserData.class));
    }

    // SUBSTANTIVE LOGIC

    public static class Credentials {
        public String email;
        public String password;
    }

    public static class UserData {
        public String email;
        public String password;
        public String name;
        public String location;
        public String birthday;
        public String signupdate;
    }

    public static String getCurrentUser() {
        return session("user-email");
    }

    public Result loginSubmit() {
        Form<Credentials> loginForm = form(Credentials.class).bindFromRequest();
        if (UserAccountManager.userRegistered(loginForm.get().email)) {
            if (UserAccountManager.authenticate(loginForm.get())) { // success
                session("user-email", loginForm.get().email);
                return harmonizepage();
            } else { // Incorrect password
                return login("Incorrect password for that email address.");
            }
        } else { // New user
            session("candidate-email", loginForm.get().email);
            session("candidate-password", loginForm.get().password);
            session("registration-in-progress", "true");
            return ok(views.html.signuppage.render(form(UserData.class)));
            //return login("Sorry -- signups are still closed!");
        }
    }

    public Result signupSubmit() {
        if (!session("registration-in-progress").equals("true")) return index();
        session("registration-in-progress", "false");
        Form<UserData> suForm = form(UserData.class).bindFromRequest();
        UserData data = suForm.get();
        data.email = session("candidate-email");
        data.password = session("candidate-password");

        UserAccountManager.registerUser(data);
        session("user-email", data.email);
        return index();
    }

    /**
     * Checks the session cookie to see if a valid username is stored there.
     */
    public static boolean validSessionIsActive() {
        String email = session("user-email");
        return UserAccountManager.userRegistered(email);
    }

    public Result harmonize(String input) {
        String email = session("user-email");
        if (!validSessionIsActive())
            return badRequest("No user is currently signed in. Try closing and reopening the site.");
        try {
            input = URLDecoder.decode(input, "UTF-8");
        } catch (Exception e) {
            UserAccountManager.writeLog(email, "harmonize", input, e.getMessage(), false);
            return badRequest("The server received an unsupported URL encoding.");
        }
        List<String> inputChords = Arrays.asList(input.split(" "));
        List<List<String>> result = new ArrayList<>();
        String str = "";
        for (String s : inputChords) str = str + s + " ";
        try {
            result = Harmonizer.harmonize(str);
        } catch (Exception e) { // Harmonizing failed for some reason
            UserAccountManager.writeLog(email, "harmonize", input, e.getMessage(), false);
            return badRequest(e.getMessage());
        }
        UserAccountManager.writeLog(email, "harmonize", input, Json.toJson(result).toString(), true);
        return ok(Json.toJson(result));
    }
}


