package id.fajarproject.roommovie.models

import com.google.gson.annotations.SerializedName

data class KeywordsItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)