package com.sajawal.imdb;

import java.util.List;

public class Production_Movie_Model {
List<Production_Movie> results;

    public Production_Movie_Model(){

    }

    public Production_Movie_Model(List<Production_Movie> results) {
        this.results = results;
    }

    public List<Production_Movie> getResults() {
        return results;
    }

    public void setResults(List<Production_Movie> results) {
        this.results = results;
    }
}
