package net.squarelabs;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    public static final String meetupHost = "http://api.meetup.com";
    public static String meetupKey;
    // Get groups
    //String groupsPath = "/2/groups?key=" + meetupKey + "&category_id=34&zip=80202&radius=50";
    public static final int[] groups = {163708, 223561, 314479, 1220063, 1422724, 1425503, 1491015, 1516743, 1535331, 1547640, 1548991, 1624468, 1657344,
            1661337, 1672600, 1674002, 1678404, 1696476, 1705510, 1714328, 1725924, 1728598, 1759946, 1769691, 1779796, 1788222, 1795116,
            1812077, 1813752, 1815835, 1817878, 2455392, 2636792, 2638312, 2782962, 3020082, 3027382, 3037962, 3140592, 3155392, 3165832,
            3271892, 3434322, 3466642, 3965112, 4006342, 4097442, 4175302, 4286122, 4339692, 4501642, 5249432, 5361852, 5463922, 5748062,
            5799632, 5883592, 6134942, 6285882, 6402972, 6576002, 6658252, 6904452, 7027362, 7193722, 7305192, 7349682, 7402532, 7468782,
            7553982, 7625212, 7787102, 7820832, 7958962, 7994132, 8311872, 8356622, 8430232, 8506532, 8998462, 9032912, 9226222, 9259062,
            9429182, 9677392, 9737702, 10230622, 10232252, 10271262, 10288712, 10330622, 10433952, 10512292, 10550012, 10715282, 10768892};
    private static Pattern memberPat = Pattern.compile("http://www\\.meetup\\.com/members/(\\d*)");
    private static HashMap<Integer, Vertex> map = new HashMap<>();
    private static TitanGraph graph;

    public static void main(String[] args) throws Exception {
        meetupKey = args[0];
        String outPath = args[1];

        // Create graph
        Configuration conf = new BaseConfiguration();
        conf.setProperty("storage.backend", "inmemory");
        graph = TitanFactory.open(conf);

        for (int groupId : groups) {
            addGroup(groupId);
        }

        // Write
        writeFile(graph, outPath);
        graph.shutdown();
    }

    private static void addGroup(int groupId) throws Exception {
        System.out.println("group=" + groupId);
        String groupPath = "/2/groups?key=" + meetupKey + "&group_id=" + groupId;
        JSONObject groupObj = (JSONObject) ((JSONArray) req(meetupHost + groupPath).get("results")).get(0);
        Vertex groupVert = addVertex(groupObj, "group");

        // members
        Integer count = null;
        int processedCount = 0;
        while(count == null || processedCount < count) {
            int pageIdx = (int)Math.ceil(processedCount / 200);
            String memberPath = "/2/members?key=" + meetupKey + "&group_id=" + groupId + "&offset=" + pageIdx;
            System.out.println("GET " + meetupHost + memberPath);
            JSONObject response = req(meetupHost + memberPath);
            JSONObject meta = (JSONObject)response.get("meta");
            count = (int)meta.get("total_count");
            JSONArray page = (JSONArray)response.get("results");
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
        graph.addEdge(null, memberVert, groupVert, "member").setProperty("relation", "member");
    }

    private static void writeFile(TitanGraph graph, String outPath) {
        try (FileOutputStream out = new FileOutputStream(new File(outPath))) {
            GraphMLWriter.outputGraph(graph, out);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Vertex addVertex(JSONObject jso, String type) {
        try {
            int id = (int) jso.get("id");
            if (map.containsKey(id))
                return map.get(id);
            Vertex vertex = graph.addVertex(id);
            vertex.setProperty("type", type);
            Iterator<?> keys = jso.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                Object val = jso.get(key);
                if ("id".equals(key))
                    key = "meetupId";
                //Class<?> clazz = val.getClass();
                vertex.setProperty(key, val);
            }
            map.put(id, vertex);
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

