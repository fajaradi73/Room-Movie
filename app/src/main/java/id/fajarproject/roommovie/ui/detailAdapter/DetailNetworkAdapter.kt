package id.fajarproject.roommovie.ui.detailAdapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.AdapterMovieDetailNetworkBinding
import id.fajarproject.roommovie.models.NetworksItem
import id.fajarproject.roommovie.ui.base.AdapterHolder
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util


/**
 * Create by Fajar Adi Prasetyo on 08/07/2020.
 */


class DetailNetworkAdapter(
    var activity: Activity,
    private var list: MutableList<NetworksItem?>
) :
    RecyclerView.Adapter<AdapterHolder<AdapterMovieDetailNetworkBinding>>() {

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterHolder<AdapterMovieDetailNetworkBinding> {
        return AdapterHolder(
            AdapterMovieDetailNetworkBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), this.onItemClickListener
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: AdapterHolder<AdapterMovieDetailNetworkBinding>,
        position: Int
    ) {
        val data = list[position] ?: NetworksItem()
        Glide.with(activity)
            .load(Constant.BASE_IMAGE + data.logoPath)
            .error(R.drawable.ic_placeholder)
            .placeholder(Util.circleLoading(activity))
            .into(holder.binding.ivPicture)
    }

    override fun getItemCount(): Int {
        return if (list.size > 5) 5 else list.size
    }

    fun getItem(position: Int): NetworksItem? {
        return list[position]
    }

}