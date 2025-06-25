package id.fajarproject.roommovie.models.people

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Parcel
data class KnownForItem(

	@field:SerializedName("overview")
	var overview: String? = null,

	@field:SerializedName("original_language")
	var originalLanguage: String? = null,

	@field:SerializedName("original_title")
	var originalTitle: String? = null,

	@field:SerializedName("original_name")
	var originalName: String? = null,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("video")
	var video: Boolean? = null,

	@field:SerializedName("title")
	var title: String? = null,

	@field:SerializedName("poster_path")
	var posterPath: String? = null,

	@field:SerializedName("backdrop_path")
	var backdropPath: String? = null,

	@field:SerializedName("media_type")
	var mediaType: String? = null,

	@field:SerializedName("release_date")
	var releaseDate: String? = null,

	@field:SerializedName("vote_average")
	var voteAverage: Double? = null,

	@field:SerializedName("id")
	var id: Int? = null,

	@field:SerializedName("adult")
	var adult: Boolean? = null,

	@field:SerializedName("vote_count")
	var voteCount: Int? = null
) : Parcelable {
	constructor(parcel: android.os.Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readValue(Double::class.java.classLoader) as? Double,
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
		parcel.readValue(Int::class.java.classLoader) as? Int
	)

	override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
		parcel.writeString(overview)
		parcel.writeString(originalLanguage)
		parcel.writeString(originalTitle)
		parcel.writeString(originalName)
		parcel.writeString(name)
		parcel.writeValue(video)
		parcel.writeString(title)
		parcel.writeString(posterPath)
		parcel.writeString(backdropPath)
		parcel.writeString(mediaType)
		parcel.writeString(releaseDate)
		parcel.writeValue(voteAverage)
		parcel.writeValue(id)
		parcel.writeValue(adult)
		parcel.writeValue(voteCount)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<KnownForItem> {
		override fun createFromParcel(parcel: android.os.Parcel): KnownForItem {
			return KnownForItem(parcel)
		}

		override fun newArray(size: Int): Array<KnownForItem?> {
			return arrayOfNulls(size)
		}
	}
}