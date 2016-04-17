package net.squarelabs.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Topic {
  String urlKey, name;
  long id;

  @JsonProperty("urlkey")
  public String getUrlKey() {
    return urlKey;
  }

  public void setUrlKey(String urlKey) {
    this.urlKey = urlKey;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
