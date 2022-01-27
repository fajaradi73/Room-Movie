package id.fajarproject.roommovie.ui.picture

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.AdapterImageBinding
import id.fajarproject.roommovie.models.PicturesItem
import id.fajarproject.roommovie.ui.base.AdapterHolder
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util

/**
 * Create by Fajar Adi Prasetyo on 13/07/2020.
 */
class PictureAdapter(
    var activity: Activity,
    private var list: MutableList<PicturesItem?>
) :
    RecyclerView.Adapter<AdapterHolder<AdapterImageBinding>>() {

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterHolder<AdapterImageBinding> {
        return AdapterHolder(
            AdapterImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), this.onItemClickListener
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: AdapterHolder<AdapterImageBinding>,
        position: Int
    ) {
        val data = list[position] ?: PicturesItem()
        Glide.with(activity)
            .load(Constant.BASE_IMAGE + data.filePath)
            .error(R.drawable.ic_placeholder)
            .placeholder(Util.circleLoading(activity))
            .into(holder.binding.ivVideo)
        holder.binding.ivPlay.visibility = View.GONE
        val transitionName = activity.getString(R.string.transition_title, position)
        holder.itemView.tag = transitionName
        ViewCompat.setTransitionName(holder.itemView, transitionName)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getItem(position: Int): PicturesItem? {
        return list[position]
    }

}