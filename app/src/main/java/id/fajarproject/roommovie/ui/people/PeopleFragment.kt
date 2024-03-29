package id.fajarproject.roommovie.ui.people

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.facebook.shimmer.Shimmer
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.FragmentPeopleBinding
import id.fajarproject.roommovie.di.component.DaggerFragmentComponent
import id.fajarproject.roommovie.di.module.FragmentModule
import id.fajarproject.roommovie.models.people.PeopleItem
import id.fajarproject.roommovie.ui.base.BaseFragment
import id.fajarproject.roommovie.ui.peopleDetail.PeopleDetailActivity
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.PaginationScrollListener
import id.fajarproject.roommovie.util.Util
import javax.inject.Inject

/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
class PeopleFragment : BaseFragment(), PeopleContract.View {

    @Inject
    lateinit var presenter: PeopleContract.Presenter
    lateinit var layoutManager: GridLayoutManager
    var adapter: PeopleAdapter? = null
    var isLoading = false
    var isLastPage = false

    private var countData = 0
    var currentPage = 1
    var limit = 20
    private lateinit var fragmentPeopleBinding: FragmentPeopleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependency()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentPeopleBinding = FragmentPeopleBinding.inflate(inflater, container, false)
        return fragmentPeopleBinding.root
    }

    override fun onDestroy() {
        presenter.unsubscribe()
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attach<Fragment>(this, this)

        setRecycleView()
        setUI()
        presenter.loadData(currentPage)
    }

    override fun setRecycleView() {
        val mNoOfColumns = Util.calculateNoOfColumns(requireContext())
        layoutManager = GridLayoutManager(requireContext(), mNoOfColumns)
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
        fragmentPeopleBinding.rvPopular.layoutManager = layoutManager
    }

    override fun setScrollRecycleView() {
        fragmentPeopleBinding.rvPopular.addOnScrollListener(object :
            PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1
                presenter.loadData(currentPage)
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
                    fragmentPeopleBinding.btnBackToTop.show()
                else
                    fragmentPeopleBinding.btnBackToTop.hide()
            }

        })
    }

    override fun showDataSuccess(list: MutableList<PeopleItem?>) {
        countData = list.size
        if (currentPage == 1) {
            adapter = PeopleAdapter(
                requireActivity(), list
            )
            fragmentPeopleBinding.rvPopular.adapter = adapter
            setScrollRecycleView()
        } else {
            adapter?.removeLoadingFooter()
            isLoading = false
            adapter?.addData(list)
            adapter?.notifyDataSetChanged()
        }

        adapter?.setOnItemClickListener(object :
            OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter?.getItem(position)
                item?.id.let {
                    val intent = Intent(requireContext(), PeopleDetailActivity::class.java)
                    intent.putExtra(Constant.idPeople, it)
                    startActivity(intent)
                }
            }
        })
        checkLastData()
        checkData()
    }

    override fun showDataFailed(message: String) {
        Log.d("ErrorFavorite", message)
        showData(false)
    }

    override fun checkLastData() {
        if (countData == limit) {
            adapter?.addLoadingFooter()
        } else {
            isLastPage = true
        }
    }

    override fun checkData() {
        if (adapter?.itemCount == 0) {
            showData(false)
        } else {
            showData(true)
        }
    }

    override fun showData(isShow: Boolean) {
        if (isShow) {
            fragmentPeopleBinding.viewPopular.visibility = View.VISIBLE
            fragmentPeopleBinding.noData.visibility = View.GONE
        } else {
            fragmentPeopleBinding.viewPopular.visibility = View.GONE
            fragmentPeopleBinding.noData.visibility = View.VISIBLE
        }
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
        fragmentPeopleBinding.btnBackToTop.hide()
        fragmentPeopleBinding.btnBackToTop.setOnClickListener {
            fragmentPeopleBinding.rvPopular.smoothScrollToPosition(
                0
            )
        }
        fragmentPeopleBinding.refreshLayout.setOnRefreshListener {
            fragmentPeopleBinding.refreshLayout.isRefreshing = false
            currentPage = 1
            presenter.loadData(currentPage)
        }
    }

    override fun showLoading() {
        fragmentPeopleBinding.shimmerView.visibility = View.VISIBLE
        fragmentPeopleBinding.shimmerView.setShimmer(
            Shimmer.AlphaHighlightBuilder().setDuration(1150L).build()
        )
        fragmentPeopleBinding.shimmerView.startShimmer()
        fragmentPeopleBinding.refreshLayout.visibility = View.GONE
        fragmentPeopleBinding.noData.visibility = View.GONE
    }

    override fun hideLoading() {
        fragmentPeopleBinding.shimmerView.stopShimmer()
        fragmentPeopleBinding.shimmerView.visibility = View.GONE
        fragmentPeopleBinding.refreshLayout.visibility = View.VISIBLE
    }

}