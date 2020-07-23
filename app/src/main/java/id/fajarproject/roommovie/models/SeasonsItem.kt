package id.fajarproject.roommovie.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Parcel
data class SeasonsItem(

	@field:SerializedName("air_date")
	val airDate: String? = null,

	@field:SerializedName("overview")
	val overview: String? = null,

	@field:SerializedName("episode_count")
	val episodeCount: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("season_number")
	val seasonNumber: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("poster_path")
	val posterPath: String? = null
) : Parcelable {
	constructor(parcel: android.os.Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString()
	)

	override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
		parcel.writeString(airDate)
		parcel.writeString(overview)
		parcel.writeValue(episodeCount)
		parcel.writeString(name)
		parcel.writeValue(seasonNumber)
		parcel.writeValue(id)
		parcel.writeString(posterPath)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<SeasonsItem> {
		override fun createFromParcel(parcel: android.os.Parcel): SeasonsItem {
			return SeasonsItem(parcel)
		}

		override fun newArray(size: Int): Array<SeasonsItem?> {
			return arrayOfNulls(size)
		}
	}
}