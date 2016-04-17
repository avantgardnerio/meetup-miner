package net.squarelabs;

import org.apache.commons.io.IOUtils;

import java.net.URL;

import static spark.Spark.get;
import static spark.Spark.staticFileLocation;

public class WebApp {
  private static final String BASE_URL = "https://api.meetup.com/";
  private static final String GET_CATEGORIES = BASE_URL + "2/categories";

  public static void main(String[] args) {
    staticFileLocation("/public"); // Static files

    get("/hello", (req, res) -> "Hello World");
    get("/categories", (req, res) -> {
      String key = req.queryParams("key");
      res.type("application/json");
      return IOUtils.toString(new URL(GET_CATEGORIES + "?key=" + key));
    });

    System.out.println("Web app started! Please browse to http://localhost:4567/");
  }
}
