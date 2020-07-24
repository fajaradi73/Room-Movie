package id.fajarproject.roommovie.ui.seasons

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.Shimmer
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.models.SeasonsItem
import id.fajarproject.roommovie.ui.base.BaseActivity
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import kotlinx.android.synthetic.main.activity_season.*
import org.parceler.Parcels

/**
 * Create by Fajar Adi Prasetyo on 23/07/2020.
 */

class SeasonActivity : BaseActivity(),SeasonContract.View {

    lateinit var adapter: SeasonAdapter
    var list : MutableList<SeasonsItem?> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_season)
        injectDependency()
        setToolbar()
        setUI()
        title       = intent.getStringExtra(Constant.title)
        list        = Parcels.unwrap(intent.getParcelableExtra(Constant.season))
        showLoading()
        if (list.isNotEmpty()){
            showDataSuccess(list)
        }else{
            checkData(false)
        }
    }

    override fun showDataSuccess(list: MutableList<SeasonsItem?>) {
        checkData(true)
        rvSeason.layoutManager  = LinearLayoutManager(this)
        adapter                 = SeasonAdapter(this,list,title.toString())
        rvSeason.adapter        = adapter

        setViewBackToTop()
    }

    override fun showDataFailed(message: String) {
        Log.e("ErrorSeason",message)
        checkData(false)
    }

    override fun checkData(isShow: Boolean) {
        hideLoading()
        if (isShow){
            refreshLayout.visibility    = View.VISIBLE
            noData.visibility           = View.GONE
        }else{
            refreshLayout.visibility    = View.GONE
            noData.visibility           = View.VISIBLE
        }
    }

    override fun setViewBackToTop() {
        rvSeason.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisibleItem    = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                val lastVisibleItem     = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (lastVisibleItem < adapter.itemCount){
                    btnBackToTop.show()
                }else{
                    btnBackToTop.hide()
                }
                if (firstVisibleItem == 0){
                    btnBackToTop.hide()
                }
            }
        })
        btnBackToTop.setOnClickListener {
            rvSeason.smoothScrollToPosition(0)
        }
    }

    override fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .build()

        activityComponent.inject(this)
    }

    override fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Util.setColorFilter(toolbar.navigationIcon!!, ContextCompat.getColor(this, R.color.iconColorPrimary))
    }

    override fun setUI() {
        btnBackToTop.hide()
        btnBackToTop.setOnClickListener { rvSeason.smoothScrollToPosition(0) }
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
        }
    }

    override fun showLoading() {
        shimmerView.visibility  = View.VISIBLE
        shimmerView.setShimmer(Shimmer.AlphaHighlightBuilder().setDuration(1150L).build())
        shimmerView.startShimmer()
        refreshLayout.visibility = View.GONE
    }

    override fun hideLoading() {
        shimmerView.stopShimmer()
        shimmerView.visibility      = View.GONE
        refreshLayout.visibility    = View.VISIBLE
    }
}