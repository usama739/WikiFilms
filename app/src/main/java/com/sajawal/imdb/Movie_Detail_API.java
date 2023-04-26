package com.sajawal.imdb;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface Movie_Detail_API {
    //String API_KEY="k_586s0xre";
    //String API_KEY="k_k62c6log";
    //String API_KEY="k_l4y51478";
   //String API_KEY = "k_hczqf67k";
    //String API_KEY="k_45if3m0x";
    //String API_KEY="k_xvps5a6v";
    //String API_KEY = "k_qzerwb64";

    //@GET(API_KEY+"/{id}/Trailer")
    //String API_KEY = "k_1k0h5qtl";
    //String API_KEY_1 = "k_45if3m0x";
    //String API_KEY_2 = "k_7st42o6q";

    String API_KEY = "k_xvps5a6v";
    String API_KEY_1 = "k_1k0h5qtl";
    String API_KEY_2 = "k_45if3m0x";
    String API_KEY_3 = "k_7st42o6q";
    String API_KEY_4 = "k_qzerwb64";
    String API_KEY_5 = "k_k62c6log";

    @GET(API_KEY_5 + "/{id}/Trailer")
    Call<Movie_Detail_Model> getMovieDetails(@Path("id") String movieId);

    @GET(API_KEY)
    Call<Production_Movie_Model> getProductionMovies(@Query("title_type") String p1,@Query("companies") String p2);

}
