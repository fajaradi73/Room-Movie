package id.fajarproject.roommovie.api

import id.fajarproject.roommovie.models.*
import id.fajarproject.roommovie.models.people.People
import id.fajarproject.roommovie.models.people.PeopleItem
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import io.reactivex.Observable
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
interface ApiServiceInterface {

    @GET("movie/now_playing")
    fun getMovieTheater(@Query("api_key") apiKey : String, @Query("page") page : Int) : Observable<Movie>

    @GET("movie/popular")
    fun getMoviePopular(@Query("api_key") apiKey : String, @Query("page") page : Int) : Observable<Movie>

    @GET("movie/top_rated")
    fun getMovieTopRated(@Query("api_key") apiKey : String, @Query("page") page : Int) : Observable<Movie>

    @GET("movie/upcoming")
    fun getMovieUpcoming(@Query("api_key") apiKey : String, @Query("page") page : Int) : Observable<Movie>

    @GET("trending/movie/day")
    fun getMovieTrending(@Query("api_key") apiKey : String, @Query("page") page : Int) : Observable<Movie>

    @GET("genre/movie/list")
    fun getMovieGenres(@Query("api_key") apiKey : String) : Observable<Genre>

    @GET("search/movie")
    fun getMovieSearch(@Query("api_key") apiKey : String, @Query("page") page : Int, @Query("query") query : String) : Observable<Movie>

    @GET("movie/{movie_id}")
    fun getMovieDetail(@Path("movie_id") movie_id : Int, @Query("api_key") apiKey: String, @Query("append_to_response") appendToResponse: String) : Observable<MovieItem>

    @GET("person/popular")
    fun getPeoplePopular(@Query("api_key") apiKey : String, @Query("page") page : Int) : Observable<People>

    @GET("person/{person_id}")
    fun getPeopleDetail(@Path("person_id") movie_id : Int, @Query("api_key") apiKey: String, @Query("append_to_response") appendToResponse: String) : Observable<PeopleItem>

    @GET("search/person")
    fun getPeopleSearch(@Query("api_key") apiKey : String, @Query("page") page : Int, @Query("query") query : String) : Observable<People>

    @GET("tv/popular")
    fun getTvPopular(@Query("api_key") apiKey : String, @Query("page") page : Int) : Observable<Movie>

    @GET("tv/top_rated")
    fun getTvTopRated(@Query("api_key") apiKey : String, @Query("page") page : Int) : Observable<Movie>

    @GET("tv/airing_today")
    fun getTvAiringToday(@Query("api_key") apiKey : String, @Query("page") page : Int) : Observable<Movie>

    @GET("tv/on_the_air")
    fun getTvOnTheAir(@Query("api_key") apiKey : String, @Query("page") page : Int) : Observable<Movie>

    @GET("search/tv")
    fun getTvSearch(@Query("api_key") apiKey : String, @Query("page") page : Int, @Query("query") query : String) : Observable<Movie>

    @GET("tv/{tv_id}")
    fun getTvDetail(@Path("tv_id") movie_id : Int, @Query("api_key") apiKey: String, @Query("append_to_response") appendToResponse: String) : Observable<MovieItem>

    @GET("genre/tv/list")
    fun getTvGenres(@Query("api_key") apiKey : String) : Observable<Genre>

    @GET("configuration/languages")
    fun getLanguages(@Query("api_key") apiKey : String) : Observable<MutableList<LanguagesItem?>?>

    @GET("movie/{movie_id}/videos")
    fun getMovieVideo(@Path("movie_id") movieId : Int, @Query("api_key") apiKey: String) : Observable<Videos>

    @GET("tv/{tv_id}/videos")
    fun getTvVideo(@Path("tv_id") tvId : Int, @Query("api_key") apiKey: String) : Observable<Videos>

    @GET("discover/tv")
    fun getTvDiscover(@Query("api_key") apiKey : String, @Query("sort_by") sortBy : String, @Query("with_genres") withGenres : String, @Query("with_keywords") withKeywords : String, @Query("with_networks") withNetworks : String, @Query("page") page : Int) : Observable<Movie>

    @GET("discover/movie")
    fun getMovieDiscover(@Query("api_key") apiKey : String, @Query("sort_by") sortBy : String, @Query("with_genres") withGenres : String, @Query("with_keywords") withKeywords : String, @Query("page") page : Int) : Observable<Movie>

    @GET("tv/{tv_id}/credits")
    fun getTvCredits(@Path("tv_id") tvId : Int, @Query("api_key") apiKey: String) : Observable<Credits>

    @GET("movie/{movie_id}/credits")
    fun getMovieCredits(@Path("movie_id") tvId : Int, @Query("api_key") apiKey: String) : Observable<Credits>

    companion object Factory {
        fun create(): ApiServiceInterface {
            val retrofit = retrofit2.Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(Util.getOkHttp())
                .baseUrl(Constant.BASE_URL)
                .build()
            return retrofit.create(ApiServiceInterface::class.java)
        }

    }

}