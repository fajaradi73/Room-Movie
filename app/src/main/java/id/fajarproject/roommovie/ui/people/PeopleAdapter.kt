package id.fajarproject.roommovie.ui.people

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.AdapterLoadingBinding
import id.fajarproject.roommovie.databinding.AdapterPeopleBinding
import id.fajarproject.roommovie.models.people.KnownForItem
import id.fajarproject.roommovie.models.people.PeopleItem
import id.fajarproject.roommovie.ui.base.AdapterHolder
import id.fajarproject.roommovie.ui.base.LoadingViewHolder
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util

/**
 * Create by Fajar Adi Prasetyo on 03/07/2020.
 */
class PeopleAdapter(
    var activity: Activity,
    private var list: MutableList<PeopleItem?>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isLoadingAdded = false

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Constant.VIEW_TYPE_ITEM) {
            AdapterHolder(
                AdapterPeopleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), this.onItemClickListener
            )
        } else {
            LoadingViewHolder(
                AdapterLoadingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val binding = AdapterPeopleBinding.bind(holder.itemView)
            val data = list[position] ?: PeopleItem()
            binding.tvName.text = data.name
            binding.tvKnownFor.text = data.knownForDepartment + " • " + getKnowFor(data.knownFor)
            Glide.with(activity)
                .load(Constant.BASE_IMAGE + data.profilePath)
                .error(R.drawable.ic_placeholder)
                .placeholder(Util.circleLoading(activity))
                .into(binding.ivProfile)
        } else {
            Log.d("Loading", ".....")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun getKnowFor(list: MutableList<KnownForItem?>?): String {
        var knowFor = ""
        if (list != null) {
            for (i in list.indices) {
                knowFor += if (i != list.lastIndex) {
                    if (list[i]?.title.isNullOrEmpty()) {
                        list[i]?.name + ", "
                    } else {
                        list[i]?.title + ", "
                    }
                } else {
                    if (list[i]?.title.isNullOrEmpty()) {
                        list[i]?.name
                    } else {
                        list[i]?.title
                    }
                }
            }
        }
        return knowFor
    }

    fun getItemList(): List<PeopleItem?> {
        return list
    }

    fun addData(movieItems: List<PeopleItem?>) {
        list.addAll(movieItems)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): PeopleItem? {
        return list[position]
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        list.add(PeopleItem())
        notifyItemInserted(list.size - 1)
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position = list.size - 1
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == list.size - 1 && isLoadingAdded) Constant.VIEW_TYPE_LOADING else Constant.VIEW_TYPE_ITEM
    }

}