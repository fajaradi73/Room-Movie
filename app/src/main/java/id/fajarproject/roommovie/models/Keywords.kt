package id.fajarproject.roommovie.models

import com.google.gson.annotations.SerializedName

data class Keywords(

	@field:SerializedName("keywords")
	val keywords: MutableList<KeywordsItem?>? = null,

	@field:SerializedName("results")
	val results: MutableList<KeywordsItem?>? = null
)