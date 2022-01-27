package id.fajarproject.roommovie.ui.peopleDetail

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.AdapterPeopleDetailCreditsBinding
import id.fajarproject.roommovie.models.CreditsItem
import id.fajarproject.roommovie.ui.base.AdapterHolder
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Util

/**
 * Create by Fajar Adi Prasetyo on 14/07/2020.
 */

class PeopleDetailCreditsAdapter(
    var activity: Activity,
    private var list: MutableList<CreditsItem?>
) :
    RecyclerView.Adapter<AdapterHolder<AdapterPeopleDetailCreditsBinding>>() {

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterHolder<AdapterPeopleDetailCreditsBinding> {
        return AdapterHolder(
            AdapterPeopleDetailCreditsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), this.onItemClickListener
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: AdapterHolder<AdapterPeopleDetailCreditsBinding>,
        position: Int
    ) {
        val data = list[position] ?: CreditsItem()
        holder.binding.tvYear.text =
            Util.convertDate(
                data.releaseDate ?: data.firstAirDate ?: "",
                "yyyy-MM-dd",
                "yyyy"
            )

        holder.binding.tvTitle.text = data.title ?: data.name
        data.episodeCount?.let {
            Util.setSpannable(
                holder.binding.tvTitle,
                " ($it ${activity.getString(R.string.episodes)})",
                ContextCompat.getColor(activity, R.color.textColorSecondary)
            )
        }
        var info = ""
        data.character?.let {
            info = "as $it"
        } ?: data.job?.let {
            info = "... $it"
        }
        holder.binding.tvInfo.text = info
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getItem(position: Int): CreditsItem? {
        return list[position]
    }

}