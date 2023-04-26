package com.sajawal.imdb;

public class Trailer {
    private String thumbnailUrl;
    private String linkEmbed;

    public Trailer(){

    }
    public Trailer(String thumbnailUrl, String linkEmbed) {
        this.thumbnailUrl = thumbnailUrl;
        this.linkEmbed = linkEmbed;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getLinkEmbed() {
        return linkEmbed;
    }

    public void setLinkEmbed(String linkEmbed) {
        this.linkEmbed = linkEmbed;
    }
}
