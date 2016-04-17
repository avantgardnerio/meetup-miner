package net.squarelabs;
import static spark.Spark.*;

/**
 * Created by rachelkoldenhoven on 4/16/16.
 */
public class WebApp {
    public static void main(String[] args) {
        staticFileLocation("/public"); // Static files
        get("/hello", (req, res) -> "Hello World");

        System.out.println("Web app started! Please browse to http://localhost:4567/");
    }
}
