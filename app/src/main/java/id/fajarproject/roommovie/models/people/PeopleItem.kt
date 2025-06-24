package id.fajarproject.roommovie.models.people

import com.google.gson.annotations.SerializedName
import id.fajarproject.roommovie.models.Credits
import id.fajarproject.roommovie.models.ExternalIds

data class PeopleItem(

    @field:SerializedName("gender")
	val gender: Int? = null,

    @field:SerializedName("known_for_department")
	val knownForDepartment: String? = null,

    @field:SerializedName("birthday")
	val birthday: String? = null,

    @field:SerializedName("biography")
	val biography: String? = null,

    @field:SerializedName("place_of_birth")
	val placeOfBirth: String? = null,

    @field:SerializedName("also_known_as")
	val alsoKnownAs: MutableList<String>? = null,

    @field:SerializedName("external_ids")
	val externalIds: ExternalIds? = null,

    @field:SerializedName("popularity")
	val popularity: Double? = null,

    @field:SerializedName("combined_credits")
	val combinedCredits: Credits? = null,

    @field:SerializedName("imdb_id")
	val imdbId: String? = null,

    @field:SerializedName("homepage")
    val homepage: String? = null,

    @field:SerializedName("known_for")
	val knownFor: MutableList<KnownForItem?>? = null,

    @field:SerializedName("name")
	val name: String? = null,

    @field:SerializedName("profile_path")
	val profilePath: String? = null,

    @field:SerializedName("id")
	val id: Int? = null,

    @field:SerializedName("adult")
	val adult: Boolean? = null
)