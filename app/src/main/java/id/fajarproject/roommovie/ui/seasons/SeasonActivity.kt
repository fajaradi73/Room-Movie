package id.fajarproject.roommovie.ui.seasons

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.Shimmer
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.ActivitySeasonBinding
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.models.SeasonsItem
import id.fajarproject.roommovie.ui.base.BaseActivity
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import org.parceler.Parcels

/**
 * Create by Fajar Adi Prasetyo on 23/07/2020.
 */

class SeasonActivity : BaseActivity(), SeasonContract.View {

    lateinit var adapter: SeasonAdapter
    var list: MutableList<SeasonsItem?> = arrayListOf()
    private lateinit var seasonBinding: ActivitySeasonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        seasonBinding = ActivitySeasonBinding.inflate(layoutInflater)
        setContentView(seasonBinding.root)
        injectDependency()
        setToolbar()
        setUI()
        title = intent.getStringExtra(Constant.title)
        list = Parcels.unwrap(intent.getParcelableExtra(Constant.season))
        showLoading()
        if (list.isNotEmpty()) {
            showDataSuccess(list)
        } else {
            checkData(false)
        }
    }

    override fun showDataSuccess(list: MutableList<SeasonsItem?>) {
        checkData(true)
        seasonBinding.rvSeason.layoutManager = LinearLayoutManager(this)
        adapter = SeasonAdapter(this, list, title.toString())
        seasonBinding.rvSeason.adapter = adapter

        setViewBackToTop()
    }

    override fun showDataFailed(message: String) {
        Log.e("ErrorSeason", message)
        checkData(false)
    }

    override fun checkData(isShow: Boolean) {
        hideLoading()
        if (isShow) {
            seasonBinding.refreshLayout.visibility = View.VISIBLE
            seasonBinding.noData.visibility = View.GONE
        } else {
            seasonBinding.refreshLayout.visibility = View.GONE
            seasonBinding.noData.visibility = View.VISIBLE
        }
    }

    override fun setViewBackToTop() {
        seasonBinding.rvSeason.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisibleItem =
                    (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                val lastVisibleItem =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (lastVisibleItem < adapter.itemCount) {
                    seasonBinding.btnBackToTop.show()
                } else {
                    seasonBinding.btnBackToTop.hide()
                }
                if (firstVisibleItem == 0) {
                    seasonBinding.btnBackToTop.hide()
                }
            }
        })
        seasonBinding.btnBackToTop.setOnClickListener {
            seasonBinding.rvSeason.smoothScrollToPosition(0)
        }
    }

    override fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .build()

        activityComponent.inject(this)
    }

    override fun setToolbar() {
        setSupportActionBar(seasonBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Util.setColorFilter(
            seasonBinding.toolbar.navigationIcon!!,
            ContextCompat.getColor(this, R.color.iconColorPrimary)
        )
    }

    override fun setUI() {
        seasonBinding.btnBackToTop.hide()
        seasonBinding.btnBackToTop.setOnClickListener {
            seasonBinding.rvSeason.smoothScrollToPosition(
                0
            )
        }
        seasonBinding.refreshLayout.setOnRefreshListener {
            seasonBinding.refreshLayout.isRefreshing = false
        }
    }

    override fun showLoading() {
        seasonBinding.shimmerView.visibility = View.VISIBLE
        seasonBinding.shimmerView.setShimmer(
            Shimmer.AlphaHighlightBuilder().setDuration(1150L).build()
        )
        seasonBinding.shimmerView.startShimmer()
        seasonBinding.refreshLayout.visibility = View.GONE
    }

    override fun hideLoading() {
        seasonBinding.shimmerView.stopShimmer()
        seasonBinding.shimmerView.visibility = View.GONE
        seasonBinding.refreshLayout.visibility = View.VISIBLE
    }
}