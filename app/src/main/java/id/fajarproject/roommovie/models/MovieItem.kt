package id.fajarproject.roommovie.models

import com.google.gson.annotations.SerializedName

data class MovieItem(

    @field:SerializedName("original_language")
	val originalLanguage: String? = null,

    @field:SerializedName("imdb_id")
	val imdbId: String? = null,

    @field:SerializedName("genre_ids")
	val genresIds : MutableList<Int>? = null,

    @field:SerializedName("videos")
	val videos: Videos? = null,

    @field:SerializedName("translations")
    val translations: Translations? = null,

    @field:SerializedName("external_ids")
    val external_ids: ExternalIds? = null,

    @field:SerializedName("video")
	val video: Boolean? = null,

    @field:SerializedName("title")
	val title: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("original_name")
    val originalName: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("backdrop_path")
	val backdropPath: String? = null,

    @field:SerializedName("revenue")
	val revenue: Int? = null,

    @field:SerializedName("number_of_episodes")
    val number_of_episodes: Int? = null,

    @field:SerializedName("number_of_seasons")
    val number_of_seasons: Int? = null,

    @field:SerializedName("genres")
	val genres: MutableList<GenresItem?>? = null,

    @field:SerializedName("popularity")
	val popularity: Double? = null,

    @field:SerializedName("production_countries")
	val productionCountries: MutableList<ProductionCountriesItem?>? = null,

    @field:SerializedName("seasons")
    val seasons: MutableList<SeasonsItem?>? = null,

    @field:SerializedName("episode_run_time")
    val episodeRunTime: MutableList<Int?>? = null,

    @field:SerializedName("id")
	val id: Int? = null,

    @field:SerializedName("vote_count")
	val voteCount: Int? = null,

    @field:SerializedName("budget")
	val budget: Int? = null,

    @field:SerializedName("overview")
	val overview: String? = null,

    @field:SerializedName("images")
	val images: Images? = null,

    @field:SerializedName("original_title")
	val originalTitle: String? = null,

    @field:SerializedName("runtime")
	val runtime: Int? = null,

    @field:SerializedName("poster_path")
	val posterPath: String? = null,

    @field:SerializedName("spoken_languages")
	val spokenLanguages: MutableList<SpokenLanguagesItem?>? = null,

    @field:SerializedName("credits")
	val credits: Casts? = null,

    @field:SerializedName("production_companies")
	val productionCompanies: MutableList<ProductionCompaniesItem?>? = null,

    @field:SerializedName("release_date")
	val releaseDate: String? = null,

    @field:SerializedName("first_air_date")
    val firstAirDate: String? = null,

    @field:SerializedName("vote_average")
	val voteAverage: Double? = null,

    @field:SerializedName("belongs_to_collection")
	val belongsToCollection: BelongsToCollection? = null,

    @field:SerializedName("tagline")
	val tagline: String? = null,

    @field:SerializedName("adult")
	val adult: Boolean? = null,

    @field:SerializedName("homepage")
	val homepage: String? = null,

    @field:SerializedName("status")
	val status: String? = null,

    @field:SerializedName("keywords")
    val keywords: Keywords? = null,

    @field:SerializedName("networks")
    val networks: MutableList<NetworksItem?>? = null,

    @field:SerializedName("recommendations")
    val recommendations: Movie? = null
)

