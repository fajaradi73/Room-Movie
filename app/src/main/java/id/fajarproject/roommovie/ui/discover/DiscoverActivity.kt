package id.fajarproject.roommovie.ui.discover

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.facebook.shimmer.Shimmer
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.ui.base.BaseActivity
import id.fajarproject.roommovie.ui.movieDetail.MovieDetailActivity
import id.fajarproject.roommovie.ui.search.SearchAdapter
import id.fajarproject.roommovie.ui.tvDetail.TvDetailActivity
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.PaginationScrollListener
import id.fajarproject.roommovie.util.Util
import kotlinx.android.synthetic.main.activity_discover.*
import javax.inject.Inject

class DiscoverActivity : BaseActivity(),DiscoverContract.Parsing,DiscoverContract.View {

    @Inject lateinit var presenter : DiscoverContract.Presenter
    lateinit var layoutManager: GridLayoutManager
    var adapter: SearchAdapter? = null
    var isLoading = false
    var isLastPage = false

    private var countData = 0
    var currentPage = 1
    var limit = 20

    private var status      : String = ""
    private var sortBy      : String = ""
    private var genre       : String = ""
    private var keywords    : String = ""
    private var networks    : String = ""
    private var isMovie = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover)

        injectDependency()
        presenter.attach<Activity>(this,this)

        status      = intent.getStringExtra(Constant.INTENT_STATUS) ?: ""
        sortBy      = intent.getStringExtra(Constant.sortBy) ?: "popularity.desc"
        genre       = intent.getStringExtra(Constant.genre) ?: ""
        keywords    = intent.getStringExtra(Constant.keywords) ?: ""
        networks    = intent.getStringExtra(Constant.networks) ?: ""
        isMovie     = intent.getBooleanExtra(Constant.isMovie,true)

        setToolbar()
        setRecycleView()
        setUI()
        if (isConnection){
            presenter.loadData(isMovie,sortBy,genre,keywords,networks,currentPage)
        }

    }

    override fun setRecycleView() {
        val mNoOfColumns = Util.calculateNoOfColumns(this)
        layoutManager = GridLayoutManager(this,mNoOfColumns)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
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
                presenter.loadData(isMovie,sortBy,genre,keywords,networks,currentPage)
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
            adapter = SearchAdapter(
                this,list,isMovie
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

    override fun moveToDetail(id: Int) {
        val intent = if (isMovie)
            Intent(this, MovieDetailActivity::class.java)
        else
            Intent(this, TvDetailActivity::class.java)

        intent.putExtra(Constant.idMovie,id)
        startActivity(intent)
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
        title = Util.toProperCaseMoreThanOneWord(status)
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
            presenter.loadData(isMovie,sortBy,genre,keywords,networks,currentPage)
        }
        cvSort.setOnClickListener {
            val sheet = DiscoverFragment(this,true)
            sheet.show(supportFragmentManager,"DiscoverFragment")
        }

        val adapter: ArrayAdapter<String?> = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.arrayType)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spType.adapter = adapter
        spType.setOnItemClickListener { _, _, position, _ ->
            isMovie = position == 0
            currentPage = 1
            presenter.loadData(isMovie,sortBy,genre,keywords,networks,currentPage)
            true
        }
        if (isMovie){
            spType.setSelection(0)
        }else{
            if (networks.isNotEmpty()){
                spType.isEnabled = false
            }
            spType.setSelection(1)
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

    override fun onPassData(data: String) {
        currentPage = 1
        presenter.loadData(isMovie,sortBy,genre,keywords,networks,currentPage)
    }

}