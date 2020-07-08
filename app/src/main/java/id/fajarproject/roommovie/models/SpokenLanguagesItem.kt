package id.fajarproject.roommovie.models

import com.google.gson.annotations.SerializedName


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */

data class SpokenLanguagesItem(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("iso_639_1")
    val iso6391: String? = null
)
