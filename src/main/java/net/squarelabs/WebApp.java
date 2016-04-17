package net.squarelabs;

import org.apache.commons.io.IOUtils;

import java.net.URL;

import static spark.Spark.get;
import static spark.Spark.staticFileLocation;

public class WebApp {
  private static final String BASE_URL = "https://api.meetup.com/";
  private static final String GET_CATEGORIES = BASE_URL + "2/categories";
  private static final String GET_GROUPS = BASE_URL + "2/groups";

  public static void main(String[] args) {
    staticFileLocation("/public"); // Static files

    get("/categories", (req, res) -> {
      String key = req.queryParams("key");
      res.type("application/json");
      return IOUtils.toString(new URL(GET_CATEGORIES + "?key=" + key));
    });

    get("/groups", (req, res) -> {
      String url = String.format("%s?key=%s&category_id=%s&zip=%s&radius=%s",
          GET_GROUPS,
          req.queryParams("key"),
          req.queryParams("category_id"),
          req.queryParams("zip"),
          req.queryParams("radius")
      );
      return IOUtils.toString(new URL(url));
    });

    System.out.println("Web app started! Please browse to http://localhost:4567/");
  }
}
