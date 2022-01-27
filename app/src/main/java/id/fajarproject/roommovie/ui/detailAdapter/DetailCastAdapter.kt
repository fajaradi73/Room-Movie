package id.fajarproject.roommovie.ui.detailAdapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.AdapterDetailCastBinding
import id.fajarproject.roommovie.models.CreditsItem
import id.fajarproject.roommovie.ui.base.AdapterHolder
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util


/**
 * Create by Fajar Adi Prasetyo on 07/07/2020.
 */


class DetailCastAdapter(
    var activity: Activity,
    private var list: MutableList<CreditsItem?>
) :
    RecyclerView.Adapter<AdapterHolder<AdapterDetailCastBinding>>() {

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterHolder<AdapterDetailCastBinding> {
        return AdapterHolder(
            AdapterDetailCastBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            this.onItemClickListener
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: AdapterHolder<AdapterDetailCastBinding>,
        position: Int
    ) {
        val data = list[position] ?: CreditsItem()
        holder.binding.tvName.text = data.name
        holder.binding.tvKnownFor.text = data.character
        Glide.with(activity)
            .load(Constant.BASE_IMAGE + data.profilePath)
            .error(R.drawable.ic_placeholder)
            .placeholder(Util.circleLoading(activity))
            .into(holder.binding.ivProfile)
    }

    override fun getItemCount(): Int {
        return if (list.size > 10) 10 else list.size
    }

    fun getItem(position: Int): CreditsItem? {
        return list[position]
    }

}