package net.squarelabs;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URLEncoder;

public class FindCompanies {
  private static TitanGraph graph;

  public static void main(String[] args) throws Exception {
    String inPath = args[0];
    String outPath = args[1];

    Configuration conf = new BaseConfiguration();
    conf.setProperty("storage.backend", "inmemory");
    graph = TitanFactory.open(conf);

    try (FileInputStream in = new FileInputStream(new File(inPath))) {
      GraphMLReader.inputGraph(graph, in);
    }

    for (Vertex vert : graph.query().vertices()) {
      String type = vert.getProperty("type");
      if(!"member".equals(type))
        continue;
      String name = URLEncoder.encode(vert.getProperty("name"));
      System.out.println(name);
      String url = "https://www.google.com/search?safe=off&q=" + name + "+site:linkedin.com&cad=h";
      Document doc = Jsoup.connect(url)
          .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
          .header("Upgrade-Insecure-Requests", "1")
          .userAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)").get();
      Elements elements = doc.select("#ires .g .slp");
      for(Element el : elements) {
        String tagline = vert.getProperty("tagline");
        if(!StringUtils.isEmpty(tagline))
          continue;
        String text = el.text();
        if(!text.contains("Denver") && !text.contains("Boulder")) {
          vert.setProperty("tagline", "Not found");
          continue;
        }
        System.out.println(text);
        vert.setProperty("tagline", text);
      }

      try (FileOutputStream out = new FileOutputStream(new File(outPath))) {
        GraphMLWriter.outputGraph(graph, out);
      }
      System.out.println("Saved!");
      Thread.sleep(4 * 60 * 1000);
    }

  }
}
