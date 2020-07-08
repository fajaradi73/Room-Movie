package id.fajarproject.roommovie.models

import com.google.gson.annotations.SerializedName


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */

data class Casts(

    @field:SerializedName("cast")
    val cast: MutableList<CastItem?>? = null,

    @field:SerializedName("crew")
    val crew: MutableList<CrewItem?>? = null
)