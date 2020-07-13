package id.fajarproject.roommovie.ui.widget

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt


/**
 * Create by Fajar Adi Prasetyo on 12/07/2020.
 */

class GridSpacingItemDecoration(
    private val context: Context,
    private val spanCount: Int,
    spacing: Int,
    includeEdge: Boolean
) :
    RecyclerView.ItemDecoration() {
    private val spacing: Int
    private val includeEdge: Boolean

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount
        if (includeEdge) {
            outRect.left = spacing - getDistanceLeft(column)
            outRect.right = getDistanceRight(column)
            if (position < spanCount) {
                outRect.top = spacing
            }
            outRect.bottom = spacing
        } else {
            outRect.left = getDistanceLeft(column)
            outRect.right = spacing - getDistanceRight(column)
            if (position >= spanCount) {
                outRect.top = spacing
            }
        }
    }

    private fun getDistanceLeft(column: Int): Int {
        return column * spacing / spanCount
    }

    private fun getDistanceRight(column: Int): Int {
        return (column + 1) * spacing / spanCount
    }

    private fun dpToPx(dp: Int): Int {
        val r: Resources = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            r.displayMetrics
        ).roundToInt()
    }

    init {
        this.spacing = dpToPx(spacing)
        this.includeEdge = includeEdge
    }
}