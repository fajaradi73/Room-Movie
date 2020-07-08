package id.fajarproject.roommovie.models

import com.google.gson.annotations.SerializedName

data class TranslationsItem(

	@field:SerializedName("overview")
	val overview: String? = null,

	@field:SerializedName("runtime")
	val runtime: Int? = null,

	@field:SerializedName("tagline")
	val tagline: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("homepage")
	val homepage: String? = null
)