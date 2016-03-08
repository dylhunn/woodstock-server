package controllers;

import play.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import harmony.core.Harmonizer;

import play.twirl.api.Content;
import views.html.*;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("Your new application is ready."));
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
        String content = "This project is maintained by Dylan D. Hunn. You can reach him by at dylhunn [at] gmail [dot] com.";
        Content html = views.html.auxtemplate.render(title, content);
        return ok(html);
    }

    public Result landing() {
        return ok(views.html.landing.render());
    }

    public Result signin() {
        return ok(views.html.signinpage.render());
    }

    public Result harmonizepage() {
        return ok(views.html.harmonize.render());
    }

    public Result harmonize(String input) {
        try {
            input = URLDecoder.decode(input, "UTF-8");
        } catch (Exception e) {
            return badRequest("The server received an unsupported URL encoding.");
        }
        List<String> inputChords = Arrays.asList(input.split(" "));
        List<List<String>> result = new ArrayList<>();
        String str = "";
        for (String s : inputChords) str = str + s + " ";
        try {
            result = Harmonizer.harmonize(str);
        } catch (Exception e) { // Harmonizing failed for some reason
            return badRequest(e.getMessage());
        }
        return ok(Json.toJson(result));
    }
}
