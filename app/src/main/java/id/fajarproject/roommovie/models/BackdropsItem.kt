package id.fajarproject.roommovie.models

import com.google.gson.annotations.SerializedName


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */

data class BackdropsItem(

    @field:SerializedName("aspect_ratio")
    val aspectRatio: Double? = null,

    @field:SerializedName("file_path")
    val filePath: String? = null,

    @field:SerializedName("vote_average")
    val voteAverage: Double? = null,

    @field:SerializedName("width")
    val width: Int? = null,

    @field:SerializedName("iso_639_1")
    val iso6391: Any? = null,

    @field:SerializedName("vote_count")
    val voteCount: Int? = null,

    @field:SerializedName("height")
    val height: Int? = null
)
