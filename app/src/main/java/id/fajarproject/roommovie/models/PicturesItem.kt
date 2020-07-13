package id.fajarproject.roommovie.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */

@Parcel
data class PicturesItem(

    @field:SerializedName("aspect_ratio")
    val aspectRatio: Double? = null,

    @field:SerializedName("file_path")
    val filePath: String? = null,

    @field:SerializedName("vote_average")
    val voteAverage: Double? = null,

    @field:SerializedName("width")
    val width: Int? = null,

    @field:SerializedName("iso_639_1")
    val iso6391: String? = null,

    @field:SerializedName("vote_count")
    val voteCount: Int? = null,

    @field:SerializedName("height")
    val height: Int? = null
) : Parcelable {
    constructor(parcel: android.os.Parcel) : this(
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
        parcel.writeValue(aspectRatio)
        parcel.writeString(filePath)
        parcel.writeValue(voteAverage)
        parcel.writeValue(width)
        parcel.writeString(iso6391)
        parcel.writeValue(voteCount)
        parcel.writeValue(height)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PicturesItem> {
        override fun createFromParcel(parcel: android.os.Parcel): PicturesItem {
            return PicturesItem(parcel)
        }

        override fun newArray(size: Int): Array<PicturesItem?> {
            return arrayOfNulls(size)
        }
    }
}
