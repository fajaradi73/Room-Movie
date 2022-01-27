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
import id.fajarproject.roommovie.databinding.FragmentTvBinding
import id.fajarproject.roommovie.databinding.ShimmerHomeBinding
import id.fajarproject.roommovie.di.component.DaggerFragmentComponent
import id.fajarproject.roommovie.di.module.FragmentModule
import id.fajarproject.roommovie.models.MovieItem
import id.fajarproject.roommovie.ui.base.BaseFragment
import id.fajarproject.roommovie.ui.tvDetail.TvDetailActivity
import id.fajarproject.roommovie.ui.tvList.TvListActivity
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import javax.inject.Inject

/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
class TvFragment : BaseFragment(), TvContract.View {

    @Inject
    lateinit var presenter: TvContract.Presenter

    private lateinit var tvBinding: FragmentTvBinding
    private lateinit var shimmerHomeBinding: ShimmerHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependency()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        tvBinding = FragmentTvBinding.inflate(inflater, container, false)
        shimmerHomeBinding = ShimmerHomeBinding.inflate(inflater, container, false)
        return tvBinding.root
    }

    override fun onDestroy() {
        presenter.unsubscribe()
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attach<Fragment>(this, this)
        presenter.loadDataAiringToday()
        setUI()
    }

    override fun moveToActivity(status: String) {
        val intent = Intent(requireContext(), TvListActivity::class.java)
        intent.putExtra(Constant.INTENT_STATUS, status)
        startActivity(intent)
    }

    override fun showDataPopularSuccess(list: MutableList<MovieItem?>) {
        tvBinding.viewPopular.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        tvBinding.rvPopular.layoutManager = layoutManager
        val adapter = TvAdapter(requireContext(), list)
        tvBinding.rvPopular.adapter = adapter
        setItemClickListener(adapter)
    }

    override fun showDataPopularFailed(message: String) {
        Log.e("ErrorPopular", message)
        tvBinding.viewPopular.visibility = View.GONE
    }

    override fun showDataAiringTodaySuccess(list: MutableList<MovieItem?>) {
        tvBinding.viewNowPlaying.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        tvBinding.rvNowPlaying.layoutManager = layoutManager
        val adapter = TvAdapter(requireContext(), list)
        tvBinding.rvNowPlaying.adapter = adapter
        setItemClickListener(adapter)
    }

    override fun showDataAiringTodayFailed(message: String) {
        Log.e("ErrorAiringToday", message)
        tvBinding.viewNowPlaying.visibility = View.GONE
    }

    override fun showDataTopRatedSuccess(list: MutableList<MovieItem?>) {
        tvBinding.viewTopRated.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        tvBinding.rvTopRated.layoutManager = layoutManager
        val adapter = TvAdapter(requireContext(), list)
        tvBinding.rvTopRated.adapter = adapter
        setItemClickListener(adapter)
    }

    override fun showDataTopRatedFailed(message: String) {
        Log.e("ErrorTopRated", message)
        tvBinding.viewTopRated.visibility = View.GONE
    }

    override fun showDataOnTheAirSuccess(list: MutableList<MovieItem?>) {
        tvBinding.viewOnTheAir.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        tvBinding.rvOnTheAir.layoutManager = layoutManager
        val adapter = TvAdapter(requireContext(), list)
        tvBinding.rvOnTheAir.adapter = adapter
        setItemClickListener(adapter)
    }

    override fun showDataOnTheAirFailed(message: String) {
        Log.e("ErrorOnTheAir", message)
        tvBinding.viewOnTheAir.visibility = View.GONE
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

    override fun moveToDetail(id: Int) {
        val intent = Intent(requireContext(), TvDetailActivity::class.java)
        intent.putExtra(Constant.idMovie, id)
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
        tvBinding.refreshLayout.isRefreshing = false
        tvBinding.refreshLayout.setOnRefreshListener {
            tvBinding.refreshLayout.isRefreshing = false
            presenter.loadDataAiringToday()
        }
        tvBinding.cvOnTheAir.setOnClickListener {
            moveToActivity(getString(R.string.tv_on_the_air))
        }
        tvBinding.cvNowPlaying.setOnClickListener {
            moveToActivity(getString(R.string.tv_airing_today))
        }
        tvBinding.cvTopRated.setOnClickListener {
            moveToActivity(getString(R.string.top_rated))
        }
        tvBinding.cvPopular.setOnClickListener {
            moveToActivity(getString(R.string.what_s_popular))
        }

        Util.changeVisibility(
            arrayOf(
                shimmerHomeBinding.llNowPlaying,
                shimmerHomeBinding.llTopRated
            ), View.GONE
        )
        Util.setViewPercents(
            requireContext(),
            arrayOf(
                shimmerHomeBinding.viewShimmer1,
                shimmerHomeBinding.viewShimmer2,
                shimmerHomeBinding.viewShimmer3,
                shimmerHomeBinding.viewShimmer4,
                shimmerHomeBinding.viewShimmer5,
                shimmerHomeBinding.viewShimmer6,
                shimmerHomeBinding.viewShimmer7,
                shimmerHomeBinding.viewShimmer8,
                shimmerHomeBinding.viewShimmer9,
                shimmerHomeBinding.viewShimmer10
            )
        )
    }

    override fun showLoading() {
        shimmerHomeBinding.scrollView.smoothScrollTo(0, 0)
        tvBinding.shimmerView.visibility = View.VISIBLE
        tvBinding.shimmerView.setShimmer(Shimmer.AlphaHighlightBuilder().setDuration(1150L).build())
        tvBinding.shimmerView.startShimmer()
        tvBinding.refreshLayout.visibility = View.GONE
    }

    override fun hideLoading() {
        tvBinding.shimmerView.stopShimmer()
        tvBinding.shimmerView.visibility = View.GONE
        tvBinding.refreshLayout.visibility = View.VISIBLE
    }

}