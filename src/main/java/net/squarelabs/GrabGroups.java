package net.squarelabs;

import com.thinkaurelius.titan.core.TitanEdge;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanVertex;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

public class GrabGroups {
  public static final String meetupHost = "http://api.meetup.com";
  public static String meetupKey;
  // Get groups
  //String groupsPath = "/2/groups?key=" + meetupKey + "&category_id=34&zip=80202&radius=50";
  public static final int[] groups = {18262332, 19134792, 163708, 314479, 1705510};
  private static Pattern memberPat = Pattern.compile("http://www\\.meetup\\.com/members/(\\d*)");
  private static TitanGraph graph;

  public static void main(String[] args) throws Exception {
    meetupKey = args[0];

    // Create graph
    Configuration conf = new BaseConfiguration();
    conf.setProperty("storage.backend", "cassandra");
    conf.setProperty("storage.hostname", System.getProperty("storage.hostname"));
    graph = TitanFactory.open(conf);

    for (int groupId : groups) {
      addGroup(groupId);
    }

    // Write
    System.out.println("Done writing groups!");
    graph.commit();
    System.out.println("Transaction complete!");
    graph.shutdown();
    System.out.println("Done!");
  }

  private static void addGroup(int groupId) throws Exception {
    System.out.println("group=" + groupId);
    String groupPath = "/2/groups?key=" + meetupKey + "&group_id=" + groupId;
    JSONObject groupObj = (JSONObject) ((JSONArray) req(meetupHost + groupPath).get("results")).get(0);
    Vertex groupVert = addVertex(groupObj, "group");

    // members
    Integer count = null;
    int processedCount = 0;
    while (count == null || processedCount < count) {
      int pageIdx = (int) Math.ceil(processedCount / 200);
      String memberPath = "/2/members?key=" + meetupKey + "&group_id=" + groupId + "&offset=" + pageIdx;
      System.out.println("GET " + meetupHost + memberPath);
      JSONObject response = req(meetupHost + memberPath);
      JSONObject meta = (JSONObject) response.get("meta");
      count = (int) meta.get("total_count");
      JSONArray page = (JSONArray) response.get("results");
      for (int i = 0; i < page.length(); i++) {
        JSONObject memberObj = (JSONObject) page.get(i);
        addMember(groupVert, memberObj);
        processedCount++;
      }
    }
  }

  private static void addMember(Vertex groupVert, JSONObject memberObj) throws Exception {
    // Add vertex
    Vertex memberVert = addVertex(memberObj, "member");
    System.out.println("member=" + memberVert.getProperty("meetupId"));

    // Add group edge
    Edge edge = memberVert.addEdge("member", groupVert);
    edge.setProperty("relation", "member");
    graph.commit();
  }

  private static Vertex addVertex(JSONObject jso, String type) {
    try {
      int id = (int) jso.get("id");
      Vertex vertex = graph.addVertex("" + id);
      vertex.setProperty("type", type);
      Iterator<?> keys = jso.keys();
      while (keys.hasNext()) {
        String key = (String) keys.next();
        Object val = jso.get(key);
        if ("id".equals(key))
          key = "meetupId";
        Class<?> clazz = val.getClass();
        if(clazz == String.class)
          vertex.setProperty(key, val);
        else if(clazz == Integer.class)
          vertex.setProperty(key, val);
        else if(clazz == Long.class)
          vertex.setProperty(key, val.toString());
        else if(clazz == Double.class)
          vertex.setProperty(key, val);
        else if(clazz == JSONArray.class)
          vertex.setProperty(key, val.toString());
        else if(clazz == JSONObject.class)
          vertex.setProperty(key, val.toString());
        else
          throw new RuntimeException("Unknown type: " + clazz);
      }
      graph.commit();
      return vertex;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private static JSONObject req(String targetURL) {
    HttpURLConnection connection = null;
    try {
      //Create connection
      URL url = new URL(targetURL);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      connection.setUseCaches(false);
      connection.setDoOutput(true);

      // Get Response
      try (InputStream is = connection.getInputStream()) {
        try (InputStreamReader isr = new InputStreamReader(is)) {
          try (BufferedReader rd = new BufferedReader(isr)) {
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
              response.append(line);
              response.append('\r');
            }
            JSONObject jso = new JSONObject(response.toString());
            return jso;
          }
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }
}

