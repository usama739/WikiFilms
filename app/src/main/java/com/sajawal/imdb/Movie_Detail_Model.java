package com.sajawal.imdb;

import java.util.List;

public class Movie_Detail_Model {
    String fullTitle;
    String releaseDate;
    String plot;
    String awards;
    String directors;
    List<Actor> actorList;
    Trailer trailer;

    public Movie_Detail_Model(){

    }

    public Movie_Detail_Model(String fullTitle, String releaseDate, String plot, String awards, String directors, List<Actor> actorList, Trailer trailer) {
        this.fullTitle = fullTitle;
        this.releaseDate = releaseDate;
        this.plot = plot;
        this.awards = awards;
        this.directors = directors;
        this.actorList = actorList;
        this.trailer = trailer;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public List<Actor> getActorList() {
        return actorList;
    }

    public void setActorList(List<Actor> actorList) {
        this.actorList = actorList;
    }

    public String getDirectors() {
        return directors;
    }

    public void setDirectors(String directors) {
        this.directors = directors;
    }

    public String getFullTitle() {
        return fullTitle;
    }

    public void setFullTitle(String fullTitle) {
        this.fullTitle = fullTitle;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public Trailer getTrailer() {
        return trailer;
    }

    public void setTrailer(Trailer trailer) {
        this.trailer = trailer;
    }
}
