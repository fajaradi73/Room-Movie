package id.fajarproject.roommovie.models

import com.google.gson.annotations.SerializedName


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */

data class Videos(

    @field:SerializedName("results")
    val results: MutableList<VideosItem?>? = null
)
