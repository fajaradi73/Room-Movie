package id.fajarproject.roommovie.ui.detailAdapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.AdapterMovieDetailPostersBinding
import id.fajarproject.roommovie.models.PicturesItem
import id.fajarproject.roommovie.ui.base.AdapterHolder
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util


/**
 * Create by Fajar Adi Prasetyo on 08/07/2020.
 */
class DetailPostersAdapter(
    var activity: Activity,
    private var list: MutableList<PicturesItem?>
) :
    RecyclerView.Adapter<AdapterHolder<AdapterMovieDetailPostersBinding>>() {

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterHolder<AdapterMovieDetailPostersBinding> {
        return AdapterHolder(
            AdapterMovieDetailPostersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), this.onItemClickListener
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: AdapterHolder<AdapterMovieDetailPostersBinding>,
        position: Int
    ) {
        val data = list[position] ?: PicturesItem()
        Glide.with(activity)
            .load(Constant.BASE_IMAGE + data.filePath)
            .error(R.drawable.ic_placeholder)
            .placeholder(Util.circleLoading(activity))
            .into(holder.binding.ivPicture)
        val transitionName = activity.getString(R.string.transition_title, position)
        holder.itemView.tag = transitionName
        ViewCompat.setTransitionName(holder.itemView, transitionName)
    }

    override fun getItemCount(): Int {
        return if (list.size > 5) 5 else list.size
    }

    fun getItem(position: Int): PicturesItem? {
        return list[position]
    }

}