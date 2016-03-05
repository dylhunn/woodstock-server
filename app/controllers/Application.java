package controllers;

import play.*;
import play.libs.Json;
import play.mvc.*;
import harmony.core.Harmonizer;

import views.html.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public Result harmonize(String input) {
        input = input.replace("%2F", "/");
        List<String> inputChords = Arrays.asList(input.split("%20"));
        List<List<String>> result = new ArrayList<>();
        String str = "";
        for (String s : inputChords) str = str + s + " ";
        try {
            result = Harmonizer.harmonize(str);
        } catch (Exception e) {
            return badRequest(e.getMessage());
        }
        return ok(Json.toJson(result));
    }

}
