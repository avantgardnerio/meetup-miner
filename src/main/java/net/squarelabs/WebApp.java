package net.squarelabs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import net.squarelabs.model.Group;
import net.squarelabs.model.GroupResponse;
import net.squarelabs.model.Member;
import org.apache.commons.io.IOUtils;
import spark.Spark;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.get;
import static spark.Spark.staticFileLocation;

public class WebApp {
  private static final String BASE_URL = "https://api.meetup.com/";
  private static final String GET_CATEGORIES = BASE_URL + "2/categories";
  private static final String GET_GROUPS = BASE_URL + "2/groups";

  public static void main(String[] args) throws Exception {

    // Super nasty Unirest initialization
    Unirest.setObjectMapper(new ObjectMapper() {
      private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
          = new com.fasterxml.jackson.databind.ObjectMapper();

      public <T> T readValue(String value, Class<T> valueType) {
        try {
          return jacksonObjectMapper.readValue(value, valueType);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

      public String writeValue(Object value) {
        try {
          return jacksonObjectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      }
    });

    // Route for static files
    staticFileLocation("/public");

    // List categories
    get("/categories", (req, res) -> {
      String key = req.queryParams("key");
      res.type("application/json");
      return IOUtils.toString(new URL(GET_CATEGORIES + "?key=" + key));
    });

    // List groups
    Spark.post("/groups", (req, res) -> {
      res.type("application/json");
      List<Group> groups = new ArrayList<>();
      JsonObject data = (JsonObject) new JsonParser().parse(req.body());
      JsonArray categoryIds = data.getAsJsonArray("categoryIds");
      for (JsonElement categoryId : categoryIds) {
        long total = Integer.MAX_VALUE;
        while (groups.size() < total) {
          int offset = (int) Math.ceil(groups.size() / 200);
          GroupResponse resp = Unirest.get(GET_GROUPS)
              .queryString("offset", offset)
              .queryString("key", data.getAsJsonPrimitive("key").getAsString())
              .queryString("zip", data.getAsJsonPrimitive("zip").getAsString())
              .queryString("radius", data.getAsJsonPrimitive("radius").getAsString())
              .queryString("category_id", categoryId.getAsString())
              .asObject(GroupResponse.class)
              .getBody();
          groups.addAll(resp.getResults());
          total = resp.getMeta().getTotalCount();
        }
      }
      String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(groups);
      return json;
    });

    // List members
    Spark.post("/members", (req, res) -> {
      res.type("application/json");
      List<Member> members = new ArrayList<>();
      JsonObject data = (JsonObject) new JsonParser().parse(req.body());
      JsonArray groupIds = data.getAsJsonArray("groupIds");
      for (JsonElement groupId : groupIds) {
        System.out.println("groupId=" + groupId);
      }
      return "fixme!";
    });

    String url = "http://localhost:4567/";
    if(Desktop.isDesktopSupported()) {
      Desktop.getDesktop().browse(URI.create(url));
    } else {
      System.out.println("Web app started! Please browse to " + url);
    }
  }
}
