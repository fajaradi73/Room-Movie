package id.fajarproject.roommovie.ui.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.AbsListView

import android.widget.ArrayAdapter
import android.widget.ImageView
import id.fajarproject.roommovie.R


/**
 * Create by Fajar Adi Prasetyo on 22/07/2020.
 */

class SpinnerImageAdapter(context: Context, private val images: Array<Int>) :
    ArrayAdapter<Int?>(context, R.layout.spinner_item_image, images) {
    private var layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getImageForPosition(position,parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getImageForPosition(position,parent)
    }

    private fun getImageForPosition(position: Int,parent: ViewGroup): View {
        val view = layoutInflater.inflate(R.layout.spinner_item_image, parent,false)
        val icon =
            view?.findViewById<View>(R.id.imageView) as ImageView
        icon.setImageResource(images[position])
        return view
    }

}