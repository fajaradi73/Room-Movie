package id.fajarproject.roommovie.models

import com.google.gson.annotations.SerializedName

data class ExternalIds(

	@field:SerializedName("imdb_id")
	val imdbId: String? = null,

	@field:SerializedName("twitter_id")
	val twitterId: String? = null,

	@field:SerializedName("facebook_id")
	val facebookId: String? = null,

	@field:SerializedName("instagram_id")
	val instagramId: String? = null
)