package id.fajarproject.roommovie.models

import com.google.gson.annotations.SerializedName


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */

data class GenresItem(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)
