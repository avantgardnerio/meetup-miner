package net.squarelabs.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Group {
  String who, urlName, state, name, joinMode, description, link, country, visibility, city, timezone;
  long lat, id, members, lon, created, utcOffset;
  double rating;
  Category category;
  Member organizer;
  Photo groupPhoto;
  List<Topic> topics;

  public String getWho() {
    return who;
  }

  public void setWho(String who) {
    this.who = who;
  }

  @JsonProperty("urlname")
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("join_mode")
  public String getJoinMode() {
    return joinMode;
  }

  public void setJoinMode(String joinMode) {
    this.joinMode = joinMode;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getVisibility() {
    return visibility;
  }

  public void setVisibility(String visibility) {
    this.visibility = visibility;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public long getLat() {
    return lat;
  }

  public void setLat(long lat) {
    this.lat = lat;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getMembers() {
    return members;
  }

  public void setMembers(long members) {
    this.members = members;
  }

  public long getLon() {
    return lon;
  }

  public void setLon(long lon) {
    this.lon = lon;
  }

  public long getCreated() {
    return created;
  }

  public void setCreated(long created) {
    this.created = created;
  }

  @JsonProperty("utc_offset")
  public long getUtcOffset() {
    return utcOffset;
  }

  public void setUtcOffset(long utcOffset) {
    this.utcOffset = utcOffset;
  }

  public double getRating() {
    return rating;
  }

  public void setRating(double rating) {
    this.rating = rating;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public Member getOrganizer() {
    return organizer;
  }

  public void setOrganizer(Member organizer) {
    this.organizer = organizer;
  }

  @JsonProperty("group_photo")
  public Photo getGroupPhoto() {
    return groupPhoto;
  }

  public void setGroupPhoto(Photo groupPhoto) {
    this.groupPhoto = groupPhoto;
  }

  public List<Topic> getTopics() {
    return topics;
  }

  public void setTopics(List<Topic> topics) {
    this.topics = topics;
  }
}
