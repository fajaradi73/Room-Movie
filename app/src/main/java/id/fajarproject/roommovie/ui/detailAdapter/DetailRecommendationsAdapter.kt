package id.fajarproject.roommovie.ui.detailAdapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.AdapterDetailRecommendationBinding
import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.ui.base.AdapterHolder
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util

/**
 * Create by Fajar Adi Prasetyo on 08/07/2020.
 */

class DetailRecommendationsAdapter(
    var activity: Activity,
    private var list: MutableList<MovieItem?>
) :
    RecyclerView.Adapter<AdapterHolder<AdapterDetailRecommendationBinding>>() {

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterHolder<AdapterDetailRecommendationBinding> {
        return AdapterHolder(
            AdapterDetailRecommendationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), this.onItemClickListener
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: AdapterHolder<AdapterDetailRecommendationBinding>,
        position: Int
    ) {
        Util.setViewPercents(activity, arrayOf(holder.binding.viewMovie), 0.8)
        val data = list[position] ?: MovieItem()
        holder.binding.tvTitle.text = data.title ?: data.name ?: ""
        holder.binding.tvRatting.text = "${data.voteAverage?.times(10)} %"
        Glide.with(activity)
            .load(Constant.BASE_IMAGE + data.posterPath)
            .error(R.drawable.ic_placeholder)
            .placeholder(Util.circleLoading(activity))
            .into(holder.binding.ivMovie)
    }

    override fun getItemCount(): Int {
        return if (list.size > 5) 5 else list.size
    }

    fun getItem(position: Int): MovieItem? {
        return list[position]
    }
}