package net.squarelabs.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Member {
  long memberId;
  String name;
  Photo photo;

  @JsonProperty("member_id")
  public long getMemberId() {
    return memberId;
  }

  public void setMemberId(long memberId) {
    this.memberId = memberId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Photo getPhoto() {
    return photo;
  }

  public void setPhoto(Photo photo) {
    this.photo = photo;
  }
}
