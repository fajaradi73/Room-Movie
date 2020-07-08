package id.fajarproject.roommovie.ui.movieList

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.facebook.shimmer.Shimmer
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.ui.movieDetail.MovieDetailActivity
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.PaginationScrollListener
import id.fajarproject.roommovie.util.Util
import kotlinx.android.synthetic.main.activity_movie_list.*
import javax.inject.Inject

/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
class MovieListActivity : AppCompatActivity(),MovieListContract.View {

    @Inject lateinit var presenter: MovieListContract.Presenter
    lateinit var layoutManager: GridLayoutManager
    var adapter: MovieListAdapter? = null
    var isLoading = false
    var isLastPage = false

    private var countData = 0
    var currentPage = 1
    var limit = 20

    private var status : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)
        injectDependency()
        presenter.attach<Activity>(this,this)

        status = intent.getStringExtra(Constant.INTENT_STATUS) ?: ""

        setToolbar()
        setRecycleView()
        setUI()
        presenter.loadData(currentPage,status)

    }

    override fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .build()

        activityComponent.inject(this)
    }

    override fun setRecycleView() {
        val mNoOfColumns = Util.calculateNoOfColumns(this)
        layoutManager = GridLayoutManager(this,mNoOfColumns)
        layoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when {
                    adapter?.getItemViewType(position) == Constant.VIEW_TYPE_LOADING -> {
                        mNoOfColumns
                    }
                    adapter?.getItemViewType(position) == Constant.VIEW_TYPE_ITEM -> {
                        1
                    }
                    else -> {
                        mNoOfColumns
                    }
                }
            }
        }
        rvMovie.layoutManager = layoutManager
    }

    override fun setScrollRecycleView() {
        rvMovie.addOnScrollListener(object : PaginationScrollListener(layoutManager){
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1
                presenter.loadData(currentPage, status)
            }

            override fun getTotalPageCount(): Int {
                return limit
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun backToTop(isShow: Boolean) {
                if (isShow)
                    btnBackToTop.show()
                else
                    btnBackToTop.hide()
            }

        })
    }

    override fun showDataSuccess(list: MutableList<MovieItem?>) {
        countData = list.size
        if (currentPage == 1){
            adapter = MovieListAdapter(
                this,list
            )
            rvMovie.adapter = adapter
            setScrollRecycleView()
        }else{
            adapter?.removeLoadingFooter()
            isLoading = false
            adapter?.addData(list)
            adapter?.notifyDataSetChanged()
        }

        adapter?.setOnItemClickListener(object :
            OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter?.getMovie(position)
                item?.id?.let {
                    moveToDetail(it)
                }
            }
        })
        checkLastData()
        checkData()
    }

    fun moveToDetail(id : Int){
        val intent = Intent(this,MovieDetailActivity::class.java)
        intent.putExtra(Constant.idMovie,id)
        startActivity(intent)
    }

    override fun showDataFailed(message: String) {
        Log.d("ErrorFavorite",message)
        showData(false)
    }

    override fun checkLastData() {
        if (countData == limit){
            adapter?.addLoadingFooter()
        }else{
            isLastPage = true
        }
    }

    override fun checkData() {
        if (adapter?.itemCount == 0){
            showData(false)
        }else{
            showData(true)
        }
    }

    override fun showData(isShow: Boolean) {
        if (isShow){
            rvMovie.visibility  = View.VISIBLE
            noData.visibility   = View.GONE
        }else{
            rvMovie.visibility  = View.GONE
            noData.visibility   = View.VISIBLE
        }
    }

    override fun getTitle(title: String) : String?{
        return when (title){
            getString(R.string.now_playing) -> {
               getString(R.string.now_playing)
            }
            getString(R.string.top_rated) -> {
                getString(R.string.top_rated)
            }
            getString(R.string.upcoming) -> {
                getString(R.string.upcoming)
            }
            getString(R.string.trending) -> {
                getString(R.string.trending)
            }
            else -> {
                getString(R.string.what_s_popular)
            }
        }
    }

    override fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Util.setColorFilter(toolbar.navigationIcon!!, ContextCompat.getColor(this, R.color.colorBlack))
        title = getTitle(status)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }


    override fun onDestroy() {
        presenter.unsubscribe()
        super.onDestroy()
    }

    override fun setUI() {
        btnBackToTop.hide()
        btnBackToTop.setOnClickListener { rvMovie.smoothScrollToPosition(0) }
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            currentPage = 1
            presenter.loadData(currentPage,status)
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