package id.fajarproject.roommovie.models

import com.google.gson.annotations.SerializedName


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */

data class Images(

    @field:SerializedName("backdrops")
    val backdrops: MutableList<BackdropsItem?>? = null,

    @field:SerializedName("posters")
    val posters: MutableList<PostersItem?>? = null
)
