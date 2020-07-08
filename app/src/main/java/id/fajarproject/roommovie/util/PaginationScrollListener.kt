package id.fajarproject.roommovie.util

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
abstract class PaginationScrollListener :
    RecyclerView.OnScrollListener {
    private var visibleThreshold = 5
    private var mLayoutManager: RecyclerView.LayoutManager
    private var firstVisibleItem: Int = 0
    private var lastVisibleItem: Int = 1

    constructor(layoutManager: LinearLayoutManager) {
        this.mLayoutManager = layoutManager
    }

    constructor(layoutManager: GridLayoutManager) {
        this.mLayoutManager = layoutManager
    }

    constructor(layoutManager: StaggeredGridLayoutManager) {
        this.mLayoutManager = layoutManager
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val totalItemCount = mLayoutManager.itemCount
        when (mLayoutManager) {
            is StaggeredGridLayoutManager -> {
                val lastVisibleItemPositions =
                    (mLayoutManager as StaggeredGridLayoutManager).findLastCompletelyVisibleItemPositions(null)
                val lastItem = (mLayoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
                // get maximum element within the list
                visibleThreshold = (mLayoutManager as StaggeredGridLayoutManager).spanCount
                lastVisibleItem  = getLastVisibleItem(lastItem)
                firstVisibleItem = getLastVisibleItem(lastVisibleItemPositions)
            }
            is GridLayoutManager -> {
                visibleThreshold    = (mLayoutManager as GridLayoutManager).childCount
                firstVisibleItem    = (mLayoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
                lastVisibleItem     = (mLayoutManager as GridLayoutManager).findLastVisibleItemPosition()
            }
            is LinearLayoutManager -> {
                visibleThreshold    = (mLayoutManager as LinearLayoutManager).childCount
                firstVisibleItem    = (mLayoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                lastVisibleItem     = (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            }
        }

        if (!isLoading() && !isLastPage()) {
            if (visibleThreshold + firstVisibleItem >= totalItemCount
                && firstVisibleItem >= 0
                && totalItemCount >= getTotalPageCount()
            ) {
                loadMoreItems()
            }
        }
        if (lastVisibleItem < totalItemCount){
            backToTop(true)
        }else{
            backToTop(false)
        }
        if (firstVisibleItem == 0){
            backToTop(false)
        }
    }

    protected abstract fun loadMoreItems()
    abstract fun getTotalPageCount(): Int
    abstract fun isLastPage(): Boolean
    abstract fun isLoading(): Boolean
    abstract fun backToTop(isShow : Boolean)

    private fun getLastVisibleItem(firstVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in firstVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = firstVisibleItemPositions[i]
            } else if (firstVisibleItemPositions[i] > maxSize) {
                maxSize = firstVisibleItemPositions[i]
            }
        }
        return maxSize
    }
}