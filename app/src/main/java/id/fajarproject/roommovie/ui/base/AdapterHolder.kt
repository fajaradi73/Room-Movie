package id.fajarproject.roommovie.ui.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import id.fajarproject.roommovie.ui.widget.OnItemClickListener


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */

class AdapterHolder<B : ViewBinding>(var binding: B, onItemClickListener: OnItemClickListener?) :
    RecyclerView.ViewHolder(binding.root),
    View.OnClickListener {
    private var onItemClickListener: OnItemClickListener? = null

    override fun onClick(v: View?) {
        onItemClickListener?.onItemClick(v, absoluteAdapterPosition)
    }

    init {
        binding.root.setOnClickListener(this)
        this.onItemClickListener = onItemClickListener
    }

}

