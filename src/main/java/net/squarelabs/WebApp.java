package net.squarelabs;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static spark.Spark.*;

public class WebApp {
  private static final String BASE_URL = "https://api.meetup.com/";
  private static final String GET_CATEGORIES = BASE_URL + "2/categories";

  public static void main(String[] args) {
    staticFileLocation("/public"); // Static files

    get("/hello", (req, res) -> "Hello World");
    get("/categories", (req, res) -> {
      String key = req.queryParams("key");
      URL url = new URL(GET_CATEGORIES + "?key=" + key);
      URLConnection con = url.openConnection();
      try (InputStream in = con.getInputStream()) {
        String encoding = con.getContentEncoding();
        encoding = encoding == null ? "UTF-8" : encoding;
        String body = IOUtils.toString(in, encoding);
        return body;
      }
    });

    System.out.println("Web app started! Please browse to http://localhost:4567/");
  }
}
