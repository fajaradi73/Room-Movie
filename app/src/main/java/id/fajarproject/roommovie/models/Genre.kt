package id.fajarproject.roommovie.models

import com.google.gson.annotations.SerializedName


/**
 * Create by Fajar Adi Prasetyo on 02/07/2020.
 */

data class Genre(
    @field:SerializedName("genres")
    val genreList : MutableList<GenresItem?>?
)