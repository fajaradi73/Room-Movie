package id.fajarproject.roommovie.models

import com.google.gson.annotations.SerializedName


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */

data class BelongsToCollection(

    @field:SerializedName("backdrop_path")
    val backdropPath: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("poster_path")
    val posterPath: String? = null
)
