package id.fajarproject.roommovie.ui.detailAdapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.models.VideosItem
import id.fajarproject.roommovie.ui.base.AdapterHolder
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import kotlinx.android.synthetic.main.adapter_detail_video.view.*

/**
 * Create by Fajar Adi Prasetyo on 08/07/2020.
 */
class DetailVideoAdapter(
    var activity: Activity,
    private var list: MutableList<VideosItem?>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onItemClickListener : OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?){
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AdapterHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(R.layout.adapter_detail_video, parent, false)
            , this.onItemClickListener
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        Util.setViewPercents(activity, arrayOf(holder.itemView.viewMovie))
        val data = list[position] ?: VideosItem()
        Glide.with(activity)
            .load(Constant.BASE_THUMBNAIL + data.key + Constant.SD_QUALITY)
            .error(R.drawable.ic_placeholder)
            .placeholder(Util.circleLoading(activity))
            .into(holder.itemView.ivVideo)
    }

    override fun getItemCount(): Int {
        return if (list.size > 5) 5 else list.size
    }

    fun getItem(position: Int): VideosItem? {
        return list[position]
    }

}