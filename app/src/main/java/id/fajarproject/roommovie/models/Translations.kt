package id.fajarproject.roommovie.models

import com.google.gson.annotations.SerializedName

data class Translations(

	@field:SerializedName("data")
	val data: TranslationsItem? = null,

	@field:SerializedName("iso_3166_1")
	val iso31661: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("iso_639_1")
	val iso6391: String? = null,

	@field:SerializedName("english_name")
	val englishName: String? = null
)