package id.fajarproject.roommovie.models

import com.google.gson.annotations.SerializedName


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */

data class Dates(

    @field:SerializedName("maximum")
    val maximum: String? = null,

    @field:SerializedName("minimum")
    val minimum: String? = null
)