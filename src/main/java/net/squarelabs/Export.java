package net.squarelabs;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.structure.io.graphml.GraphMLWriter;

import java.io.File;
import java.io.FileOutputStream;

public class Export {
  public static void main(String[] args) {
    Configuration conf = new BaseConfiguration();
    conf.setProperty("storage.backend", "cassandra");
    conf.setProperty("storage.hostname", System.getProperty("storage.hostname"));
    TitanGraph graph = TitanFactory.open(conf);

    try (FileOutputStream out = new FileOutputStream(new File("companies.graphml"))) {
      GraphMLWriter writer = GraphMLWriter.build().create();
      writer.writeGraph(out, graph);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}
