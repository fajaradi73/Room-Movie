package id.fajarproject.roommovie.ui.tv

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.shimmer.Shimmer
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.di.component.DaggerFragmentComponent
import id.fajarproject.roommovie.di.module.FragmentModule
import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.ui.tvDetail.TvDetailActivity
import id.fajarproject.roommovie.ui.tvList.TvListActivity
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_tv.*
import kotlinx.android.synthetic.main.shimmer_home.*

/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
class TvFragment : Fragment() ,TvContract.View {

    @Inject lateinit var presenter: TvContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependency()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tv, container, false)
    }

    override fun onDestroy() {
        presenter.unsubscribe()
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attach<Fragment>(this,this)
        presenter.loadDataAiringToday()
        setUI()
    }

    override fun moveToActivity(status: String) {
        val intent = Intent(requireContext(),TvListActivity::class.java)
        intent.putExtra(Constant.INTENT_STATUS,status)
        startActivity(intent)
    }

    override fun showDataPopularSuccess(list: MutableList<MovieItem?>) {
        viewPopular.visibility      = View.VISIBLE
        val layoutManager           = LinearLayoutManager(activity)
        layoutManager.orientation   = LinearLayoutManager.HORIZONTAL
        rvPopular.layoutManager     = layoutManager
        val adapter                 = TvAdapter(requireContext(),list)
        rvPopular.adapter           = adapter
        setItemClickListener(adapter)
    }

    override fun showDataPopularFailed(message: String) {
        Log.e("ErrorPopular",message)
        viewPopular.visibility      = View.GONE
    }

    override fun showDataAiringTodaySuccess(list: MutableList<MovieItem?>) {
        viewNowPlaying.visibility       = View.VISIBLE
        val layoutManager               = LinearLayoutManager(activity)
        layoutManager.orientation       = LinearLayoutManager.HORIZONTAL
        rvNowPlaying.layoutManager      = layoutManager
        val adapter                     = TvAdapter(requireContext(),list)
        rvNowPlaying.adapter            = adapter
        setItemClickListener(adapter)
    }

    override fun showDataAiringTodayFailed(message: String) {
        Log.e("ErrorAiringToday",message)
        viewNowPlaying.visibility      = View.GONE
    }

    override fun showDataTopRatedSuccess(list: MutableList<MovieItem?>) {
        viewTopRated.visibility      = View.VISIBLE
        val layoutManager            = LinearLayoutManager(activity)
        layoutManager.orientation    = LinearLayoutManager.HORIZONTAL
        rvTopRated.layoutManager     = layoutManager
        val adapter                  = TvAdapter(requireContext(),list)
        rvTopRated.adapter           = adapter
        setItemClickListener(adapter)
    }

    override fun showDataTopRatedFailed(message: String) {
        Log.e("ErrorTopRated",message)
        viewTopRated.visibility      = View.GONE
    }

    override fun showDataOnTheAirSuccess(list: MutableList<MovieItem?>) {
        viewOnTheAir.visibility     = View.VISIBLE
        val layoutManager           = LinearLayoutManager(activity)
        layoutManager.orientation   = LinearLayoutManager.HORIZONTAL
        rvOnTheAir.layoutManager    = layoutManager
        val adapter                 = TvAdapter(requireContext(),list)
        rvOnTheAir.adapter          = adapter
        setItemClickListener(adapter)
    }

    override fun showDataOnTheAirFailed(message: String) {
        Log.e("ErrorOnTheAir",message)
        viewOnTheAir.visibility      = View.GONE
    }

    override fun setItemClickListener(adapter: TvAdapter) {
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter.getItem(position)
                item.id?.let {
                    moveToDetail(it)
                }
            }
        })
    }

    override fun moveToDetail(id : Int){
        val intent = Intent(requireContext(), TvDetailActivity::class.java)
        intent.putExtra(Constant.idMovie,id)
        startActivity(intent)
    }

    override fun injectDependency() {
        val fragmentComponent = DaggerFragmentComponent.builder()
            .fragmentModule(FragmentModule())
            .build()

        fragmentComponent.inject(this)
    }

    override fun setToolbar() {
    }

    override fun setUI() {
        refreshLayout.isRefreshing = false
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            presenter.loadDataAiringToday()
        }
        cvOnTheAir.setOnClickListener {
            moveToActivity(getString(R.string.tv_on_the_air))
        }
        cvNowPlaying.setOnClickListener {
            moveToActivity(getString(R.string.tv_airing_today))
        }
        cvTopRated.setOnClickListener {
            moveToActivity(getString(R.string.top_rated))
        }
        cvPopular.setOnClickListener {
            moveToActivity(getString(R.string.what_s_popular))
        }

        Util.changeVisibility(arrayOf(llNowPlaying,llTopRated),View.GONE)
        Util.setViewPercents(requireContext(), arrayOf(viewShimmer1,viewShimmer2,viewShimmer3,viewShimmer4,viewShimmer5,viewShimmer6,viewShimmer7,viewShimmer8,viewShimmer9,viewShimmer10))
    }

    override fun showLoading() {
        scrollView.smoothScrollTo(0,0)
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