package com.sajawal.imdb.MovieSlider

import com.sajawal.imdb.Search.MovieSearchResult
import com.sajawal.imdb.WatchList.WatchListItem
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

val BASE_URL = "https://imdb-api.com/en/api/"

//const val API_KEY_1 = "k_l4y51478"
//const val API_KEY = "k_d1s26l2i"
//const val API_KEY="k_586s0xre"

//const val API_KEY = "k_l4y51478"

//const val API_KEY= "k_45if3m0x"
//const val API_KEY = "k_hczqf67k"
//const val API_KEY = "k_xvps5a6v"

//const val API_KEY= "k_aaaaaaaa"

//const val API_KEY = "k_hczqf67k"

//Working Keys
const val API_KEY = "k_xvps5a6v"
const val API_KEY_1 = "k_1k0h5qtl"
const val API_KEY_2 = "k_45if3m0x"
const val API_KEY_3 = "k_7st42o6q"
const val API_KEY_4 = "k_qzerwb64"
const val API_KEY_5 = "k_k62c6log"
//Working Keys

//const val API_KEY = "k_1k0h5qtl"
//const val API_KEY_2 = "k_45if3m0x"
//const val API_KEY_3 = "k_7st42o6q"

//const val API_KEY = "k_qzerwb64"

interface MovieAPI {
    @GET("MostPopularMovies/$API_KEY")
    fun getTrendingMovies(): Call<APIResponse>

    //@GET("ComingSoon/$API_KEY")
    @GET("ComingSoon/$API_KEY_1")
    fun getUpComingMovies(): Call<APIResponse>

    @GET("Top250Movies/$API_KEY_2")
    fun getTopMovies(): Call<APIResponse>

    @GET("SearchMovie/$API_KEY_3/{exp}")
    fun searchMovies(@Path("exp") exp: String):Call<MovieSearchResult>

    @GET("Title/$API_KEY_5/{id}")
    fun getMovieDetail(@Path("id") id: String):Call<WatchListItem>

    //https://imdb-api.com/en/API/Title/k_1k0h5qtl/tt1375666
}

object RetrofitInstance{
    val movieService: MovieAPI
    init{
        val retrofitInstance = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        movieService = retrofitInstance.create(MovieAPI::class.java)
    }

}

//https://imdb-api.com/en/api/MostPopularMovies/k_l4y51478

//https://imdb-api.com/en/api/ComingSoon/k_l4y51478