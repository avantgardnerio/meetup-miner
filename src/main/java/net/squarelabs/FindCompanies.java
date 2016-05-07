package net.squarelabs;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanVertex;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyVertexProperty;
import org.codehaus.jettison.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;

public class FindCompanies {
  private static TitanGraph graph;

  public static void main(String[] args) throws Exception {
    long sleepTime = Long.parseLong(args[0]);

    Configuration conf = new BaseConfiguration();
    conf.setProperty("storage.backend", "cassandra");
    conf.setProperty("storage.hostname", System.getProperty("storage.hostname"));
    graph = TitanFactory.open(conf);

    Iterable<TitanVertex> vertices = graph.query().vertices();
    System.out.println("Got vertices!");
    int i = 0;
    for (TitanVertex vert : vertices) {
      String type = vert.property("type").value().toString();
      System.out.println("Vertex " + i++ + " type=" + type);
      if (!"member".equals(type))
        continue;
      VertexProperty<?> prop = vert.property("tagline");
      if (!(prop instanceof EmptyVertexProperty) && prop != null && prop.value() != null && !StringUtils.isEmpty(prop.value().toString())) {
        System.out.println("Skipping member with tagline: " + prop.value().toString());
        //continue;
      }
      String name = URLEncoder.encode(vert.property("name").value().toString());
      System.out.println(name);
      String url = "https://www.google.com/search?safe=off&q=" + name + "+Denver+site:linkedin.com&cad=h&num=100";
      Thread.sleep(sleepTime);
      Document doc = Jsoup.connect(url)
          .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
          .header("Upgrade-Insecure-Requests", "1")
          .userAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)").get();
      Elements elements = doc.select("#ires .g .slp");
      if (elements.size() == 0) {
        System.out.println("Elements not found!");
        continue;
      }
      JSONArray tagLines = new JSONArray();
      for (Element el : elements) {
        String text = el.text();
        if (!text.contains("Denver")
            && !text.contains("Boulder")
            && !text.contains("Golden")
            && !text.contains("Colorado")
            ) {
          System.out.println("Skipping tagline: " + text);
          continue;
        }
        System.out.println("tagline: " + text);
        tagLines.put(text);
      }
      vert.property("tagline", tagLines.toString());

      graph.tx().commit();
      System.out.println("Saved!");
    }

  }
}
