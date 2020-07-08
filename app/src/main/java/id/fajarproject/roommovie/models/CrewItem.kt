package id.fajarproject.roommovie.models

import com.google.gson.annotations.SerializedName


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */

data class CrewItem(

    @field:SerializedName("gender")
    val gender: Int? = null,

    @field:SerializedName("credit_id")
    val creditId: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("profile_path")
    val profilePath: Any? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("department")
    val department: String? = null,

    @field:SerializedName("job")
    val job: String? = null
)
