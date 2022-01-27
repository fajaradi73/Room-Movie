package id.fajarproject.roommovie.ui.tv

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.AdapterHomeBinding
import id.fajarproject.roommovie.models.GenresItem
import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.ui.base.AdapterHolder
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util


/**
 * Create by Fajar Adi Prasetyo on 02/07/2020.
 */

class TvAdapter(private var activity: Context, private var list: MutableList<MovieItem?>) :
    RecyclerView.Adapter<AdapterHolder<AdapterHomeBinding>>() {

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterHolder<AdapterHomeBinding> {
        return AdapterHolder(
            AdapterHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), this.onItemClickListener
        )
    }

    override fun getItemCount(): Int {
        return if (list.size > 5) 5 else list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AdapterHolder<AdapterHomeBinding>, position: Int) {
        Util.setViewPercents(activity, arrayOf(holder.binding.viewMovie))
        val movie = list[position]

        Glide.with(activity)
            .load(Constant.BASE_IMAGE + movie?.backdropPath)
            .error(R.drawable.ic_placeholder)
            .placeholder(Util.circleLoading(activity))
            .into(holder.binding.ivBackground)
        Glide.with(activity)
            .load(Constant.BASE_IMAGE + movie?.posterPath)
            .error(R.drawable.ic_placeholder)
            .placeholder(Util.circleLoading(activity))
            .into(holder.binding.ivPoster)
        holder.binding.tvTitle.text = movie?.name
        holder.binding.tvRatting.text = movie?.voteAverage.toString() + " \u2605"
        holder.binding.tvGenre.text = setGenre(movie?.genresIds ?: arrayListOf())
        holder.binding.tvInfo.text =
            Util.convertDate(movie?.firstAirDate ?: "", "yyyy-MM-dd", "dd MMMM yyyy")
    }

    private fun getGenre(id: Int): String {
        var genre = ""
        val list: MutableList<GenresItem?>? = Util.getGenre(activity, Constant.genreTv)
        if (list != null) {
            for (data in list) {
                if (data?.id == id) {
                    genre = data.name ?: ""
                    break
                }
            }
        }
        return genre
    }

    private fun setGenre(list: MutableList<Int>): String {
        var nameGenre = ""
        for (i in list.indices) {
            val name = getGenre(list[i])
            if (name.isNotEmpty()) {
                nameGenre += if (i == list.size - 1) {
                    name
                } else {
                    "$name \u2022 "
                }
            }
        }
        return nameGenre
    }

    fun getItem(position: Int): MovieItem {
        return list[position] ?: MovieItem()
    }
}