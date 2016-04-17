package net.squarelabs.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Photo {
  String highresLink, photoLink, thumbLink;
  long photoId;

  @JsonProperty("highres_link")
  public String getHighresLink() {
    return highresLink;
  }

  public void setHighresLink(String highresLink) {
    this.highresLink = highresLink;
  }

  @JsonProperty("photo_link")
  public String getPhotoLink() {
    return photoLink;
  }

  public void setPhotoLink(String photoLink) {
    this.photoLink = photoLink;
  }

  @JsonProperty("thumb_link")
  public String getThumbLink() {
    return thumbLink;
  }

  public void setThumbLink(String thumbLink) {
    this.thumbLink = thumbLink;
  }

  @JsonProperty("photo_id")
  public long getPhotoId() {
    return photoId;
  }

  public void setPhotoId(long photoId) {
    this.photoId = photoId;
  }
}
