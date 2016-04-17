package net.squarelabs.model;

import java.util.List;

public class GroupResponse {
  Meta meta;
  List<Group> results;

  public Meta getMeta() {
    return meta;
  }

  public void setMeta(Meta meta) {
    this.meta = meta;
  }

  public List<Group> getResults() {
    return results;
  }

  public void setResults(List<Group> results) {
    this.results = results;
  }
}
