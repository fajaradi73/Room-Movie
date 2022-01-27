package id.fajarproject.roommovie.ui.seasons

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.AdapterSeasonBinding
import id.fajarproject.roommovie.models.SeasonsItem
import id.fajarproject.roommovie.ui.base.AdapterHolder
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util


/**
 * Create by Fajar Adi Prasetyo on 23/07/2020.
 */

class SeasonAdapter(
    var activity: Activity,
    private var list: MutableList<SeasonsItem?>,
    var title: String
) :
    RecyclerView.Adapter<AdapterHolder<AdapterSeasonBinding>>() {

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterHolder<AdapterSeasonBinding> {
        return AdapterHolder(
            AdapterSeasonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), this.onItemClickListener
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: AdapterHolder<AdapterSeasonBinding>,
        position: Int
    ) {
        val data = list[position] ?: SeasonsItem()
        val name = if (data.name != null && data.name.isNotEmpty() && !data.name.contains(title)) {
            data.name
        } else {
            "${activity.getString(R.string.seasons)} ${data.seasonNumber}"
        }

        holder.binding.tvTitle.text = name
        val date = if (data.airDate != null && data.airDate.isNotEmpty()) {
            Util.convertDate(data.airDate, "yyyy-MM-dd", "yyyy")
        } else {
            "-"
        }

        holder.binding.tvDate.text = " $date| ${data.episodeCount} ${
            activity.getString(
                R.string.episodes
            )
        }"

        val overview = if (data.overview != null && data.overview.isNotEmpty()) {
            data.overview
        } else {
            if (data.airDate != null && data.airDate.isNotEmpty()) {
                activity.getString(
                    R.string.overviewSeason, name, title,
                    Util.convertDate(data.airDate, "yyyy-MM-dd", "MMMM d,yyyy")
                )
            } else {
                activity.getString(R.string.noOverview)
            }
        }

        holder.binding.tvOverview.text = overview
        Glide.with(activity)
            .load(Constant.BASE_IMAGE + data.posterPath)
            .error(R.drawable.ic_placeholder)
            .placeholder(Util.circleLoading(activity))
            .into(holder.binding.ivSeason)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getItem(position: Int): SeasonsItem? {
        return list[position]
    }
}