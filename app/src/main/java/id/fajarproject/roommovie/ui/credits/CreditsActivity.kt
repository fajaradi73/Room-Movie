package id.fajarproject.roommovie.ui.credits

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.facebook.shimmer.Shimmer
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.models.Credits
import id.fajarproject.roommovie.ui.base.BaseActivity
import id.fajarproject.roommovie.ui.base.ViewPagerAdapter
import id.fajarproject.roommovie.ui.widget.DialogListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import kotlinx.android.synthetic.main.activity_credits.*
import javax.inject.Inject

class CreditsActivity : BaseActivity(),CreditsContract.View{

    @Inject lateinit var presenter : CreditsContract.Presenter

    private var isMovie = true
    private var status : String? = null
    private var id : Int = -1
    private lateinit var adapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)
        injectDependency()
        presenter.attach(this,this)
        isMovie = intent.getBooleanExtra(Constant.isMovie,true)
        status  = intent.getStringExtra(Constant.INTENT_STATUS)
        id      = intent.getIntExtra(Constant.idMovie,-1)
        setToolbar()
        setViewPager(viewpager)
        setUI()
        if (id == -1){
            showDataFailed("")
            return
        }

        if (isConnection){
            presenter.loadData(isMovie,id)
        }
    }

    override fun setViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(
            supportFragmentManager
        )
        adapter.addFrag(CreditsFragment(),"Cast")
        adapter.addFrag(CreditsFragment(),"Crew")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)
    }

    lateinit var viewFragment: CreditsContract.Fragment

    override fun showDataSuccess(data: Credits?) {
        viewpager.currentItem = 0
        viewpager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                if (position == 0){
                    viewFragment = adapter.getItem(position) as CreditsContract.Fragment
                    viewFragment.showData(data?.cast ?: arrayListOf())
                }else{
                    viewFragment = adapter.getItem(position) as CreditsContract.Fragment
                    viewFragment.showData(data?.crew ?: arrayListOf())
                }
            }
        })

        Handler().postDelayed(
            {
                viewFragment = adapter.getItem(0) as CreditsContract.Fragment
                viewFragment.showData(data?.cast ?: arrayListOf())
            }, 100
        )
    }

    override fun showDataFailed(message: String) {
        Util.showRoundedDialog(this,getString(R.string.no_data),message,false,object : DialogListener{
            override fun onYes() {
                finish()
            }
            override fun onNo() {
            }
        })
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
        title = status
    }

    override fun setUI() {
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            presenter.loadData(isMovie,id)
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