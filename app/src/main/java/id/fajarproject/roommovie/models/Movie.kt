package id.fajarproject.roommovie.models

import com.google.gson.annotations.SerializedName

data class Movie(

    @field:SerializedName("dates")
	val dates: Dates? = null,

    @field:SerializedName("page")
	val page: Int? = null,

    @field:SerializedName("total_pages")
	val totalPages: Int? = null,

    @field:SerializedName("results")
	val movieList: MutableList<MovieItem?>? = null,

    @field:SerializedName("total_results")
	val totalResults: Int? = null
)

