package id.fajarproject.roommovie.models.people

import com.google.gson.annotations.SerializedName

data class People(

    @field:SerializedName("page")
	val page: Int? = null,

    @field:SerializedName("total_pages")
	val totalPages: Int? = null,

    @field:SerializedName("results")
	val results: MutableList<PeopleItem?>? = null,

    @field:SerializedName("total_results")
	val totalResults: Int? = null
)