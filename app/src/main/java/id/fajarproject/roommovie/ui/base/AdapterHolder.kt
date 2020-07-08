package id.fajarproject.roommovie.ui.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import id.fajarproject.roommovie.ui.widget.OnItemClickListener


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */

class AdapterHolder(itemView: View, onItemClickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {
    private var onItemClickListener : OnItemClickListener? = null

    override fun onClick(v: View?) {
        onItemClickListener?.onItemClick(v,adapterPosition)
    }
    init {
        itemView.setOnClickListener(this)
        this.onItemClickListener = onItemClickListener
    }
}