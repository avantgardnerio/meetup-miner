package net.squarelabs;
import static spark.Spark.*;

/**
 * Created by rachelkoldenhoven on 4/16/16.
 */
public class WebApp {
    public static void main(String[] args) {
        System.out.println("Hello World");
        get("/hello", (req, res) -> "Hello World");
    }
}
