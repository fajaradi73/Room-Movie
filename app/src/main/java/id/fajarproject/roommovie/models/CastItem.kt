package id.fajarproject.roommovie.models

import com.google.gson.annotations.SerializedName


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */

data class CastItem(

    @field:SerializedName("cast_id")
    val castId: Int? = null,

    @field:SerializedName("character")
    val character: String? = null,

    @field:SerializedName("gender")
    val gender: Int? = null,

    @field:SerializedName("credit_id")
    val creditId: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("profile_path")
    val profilePath: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("order")
    val order: Int? = null
)
