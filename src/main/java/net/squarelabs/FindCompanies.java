package net.squarelabs;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanVertex;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;

public class FindCompanies {
  private static TitanGraph graph;

  public static void main(String[] args) throws Exception {
    Configuration conf = new BaseConfiguration();
    conf.setProperty("storage.backend", "cassandra");
    conf.setProperty("storage.hostname", System.getProperty("storage.hostname"));
    graph = TitanFactory.open(conf);

    Iterable<TitanVertex> vertices = graph.query().vertices();
    System.out.println("Got vertices!");
    int i = 0;
    for (TitanVertex vert : vertices) {
      System.out.println("Vertex " + i++);
      String type = vert.property("type").toString();
      if (!"member".equals(type))
        continue;
      String name = URLEncoder.encode(vert.property("name").toString());
      System.out.println(name);
      String url = "https://www.google.com/search?safe=off&q=" + name + "+site:linkedin.com&cad=h";
      Document doc = Jsoup.connect(url)
          .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
          .header("Upgrade-Insecure-Requests", "1")
          .userAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)").get();
      Elements elements = doc.select("#ires .g .slp");
      for (Element el : elements) {
        String tagline = vert.property("tagline").toString();
        if (!StringUtils.isEmpty(tagline))
          continue;
        String text = el.text();
        if (!text.contains("Denver") && !text.contains("Boulder")) {
          vert.property("tagline", "Not found");
          continue;
        }
        System.out.println(text);
        vert.property("tagline", text);
      }

      System.out.println("Saved!");
      Thread.sleep(4 * 60 * 1000);
    }

  }
}
