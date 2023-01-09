package id.fajarproject.roommovie.ui.peopleDetail

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.google.android.material.appbar.AppBarLayout
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.ActivityPeopleDetailBinding
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.models.CreditsItem
import id.fajarproject.roommovie.models.ExternalIds
import id.fajarproject.roommovie.models.people.KnownForItem
import id.fajarproject.roommovie.models.people.PeopleItem
import id.fajarproject.roommovie.ui.base.BaseActivity
import id.fajarproject.roommovie.ui.movieDetail.MovieDetailActivity
import id.fajarproject.roommovie.ui.tvDetail.TvDetailActivity
import id.fajarproject.roommovie.ui.widget.AppBarStateChangeListener
import id.fajarproject.roommovie.ui.widget.DialogListener
import id.fajarproject.roommovie.ui.widget.OnItemClickListener
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import org.parceler.Parcels
import javax.inject.Inject

class PeopleDetailActivity : BaseActivity(), PeopleDetailContract.View {

    @Inject
    lateinit var presenter: PeopleDetailContract.Presenter
    private var id = -1
    private var listKnownForItem: MutableList<KnownForItem?>? = null

    private lateinit var peopleDetailBinding: ActivityPeopleDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        peopleDetailBinding = ActivityPeopleDetailBinding.inflate(layoutInflater)
        setContentView(peopleDetailBinding.root)
        injectDependency()
        presenter.attach(this, this)
        setToolbar()
        setUI()
        id = intent.getIntExtra(Constant.idPeople, -1)
        listKnownForItem = Parcels.unwrap(intent.getParcelableExtra(Constant.dataKnownFor))

        if (id == -1) {
            showDialogNoData("")
            return
        }
        if (isConnection) {
            presenter.loadData(id)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun showDataSuccess(data: PeopleItem) {
        Glide.with(this)
            .load(Constant.BASE_IMAGE + data.profilePath)
            .placeholder(Util.circleLoading(this))
            .into(peopleDetailBinding.ivPoster)
        setOnSetChange(data.name ?: "")

        var biography = ""
        data.biography?.let {
            biography = it.ifEmpty {
                getString(R.string.biographyPeople, data.name)
            }
        } ?: kotlin.run {
            biography = getString(R.string.biographyPeople, data.name)
        }
        peopleDetailBinding.peopleDetailOverview.tvBiography.text = biography

        Util.makeTextViewResizable(
            peopleDetailBinding.peopleDetailOverview.tvBiography,
            7,
            "View More",
            true
        )

        peopleDetailBinding.peopleDetailInfo.tvGender.text = presenter.getGender(data.gender)
        var birthday = "-"
        data.birthday?.let {
            birthday = "${Util.convertDate(it, "yyyy-MM-dd", "dd MMMM yyyy")} (${
                Util.getAge(
                    it,
                    "yyyy-MM-dd"
                )
            })"
        }
        peopleDetailBinding.peopleDetailInfo.tvBirthday.text = birthday
        peopleDetailBinding.peopleDetailInfo.tvPlaceBirth.text = data.placeOfBirth
        peopleDetailBinding.peopleDetailInfo.tvKnownFor.text = data.knownForDepartment
        peopleDetailBinding.peopleDetailInfo.tvKnownAs.text = presenter.getKnowAs(data.alsoKnownAs)
        peopleDetailBinding.peopleDetailInfo.tvKnownCredits.text = "${
            presenter.getKnownCredits(data.combinedCredits?.cast)
                .plus(presenter.getKnownCredits(data.combinedCredits?.crew))
        }"
        peopleDetailBinding.peopleDetailCast.acting.text = data.knownForDepartment

        data.external_ids?.let {
            setViewExternalIDs(it)
        }
        val listCredits: MutableList<CreditsItem?> = arrayListOf()
        val listKnown: MutableList<CreditsItem?> = arrayListOf()

        if (data.knownForDepartment == "Acting") {
            data.combinedCredits?.cast?.let {
                listKnown.addAll(it)
                listCredits.addAll(it)
            }
        } else {
            data.combinedCredits?.crew?.let {
                listKnown.addAll(presenter.getKnownFor(data.knownForDepartment ?: "", it))
                listCredits.addAll(it)
            }
        }

        setViewKnownFor(listKnown)
        setViewCredits(listCredits)

        peopleDetailBinding.peopleDetailInfo.link.setOnClickListener {
            data.homepage?.let {
                if (it.isNotEmpty()) {
                    setOpenURL("https://$it/", "homepage")
                } else {
                    setOpenURL(Constant.BASE_THE_MOVIE_DB, "homepage")
                }
            } ?: kotlin.run {
                setOpenURL(Constant.BASE_THE_MOVIE_DB, "homepage")
            }
        }
        hideLoading()
    }

    override fun setOnSetChange(name: String) {
        peopleDetailBinding.appbar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                peopleDetailBinding.appbar.post {
                    if (state == State.EXPANDED) {
                        peopleDetailBinding.tvTitle.text = name
                        title = ""
                    } else if (state == State.COLLAPSED) {
                        peopleDetailBinding.tvTitle.text = ""
                        title = name
                    }
                }
            }
        })
    }

    override fun showDataFailed(message: String) {
        Log.e("ErrorDetail", message)
        showDialogNoData(message)
    }

    override fun setViewKnownFor(list: MutableList<CreditsItem?>) {
        list.sortWith(presenter.sortKnown())
//        Collections.sort(list,presenter.sortKnown())
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        peopleDetailBinding.peopleDetailKnown.rvKnownFor.layoutManager = layoutManager
        val adapter = PeopleDetailKnownForAdapter(this, list)
        peopleDetailBinding.peopleDetailKnown.rvKnownFor.adapter = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter.getItem(position)
                val intent = if (item?.media_type == "tv") {
                    Intent(this@PeopleDetailActivity, TvDetailActivity::class.java)
                } else {
                    Intent(this@PeopleDetailActivity, MovieDetailActivity::class.java)
                }
                item?.id?.let {
                    intent.putExtra(Constant.idMovie, it)
                    startActivity(intent)
                }
            }
        })
    }

    override fun setViewExternalIDs(data: ExternalIds) {
        data.facebookId?.let { s: String ->
            peopleDetailBinding.peopleDetailInfo.facebook.visibility = View.VISIBLE
            peopleDetailBinding.peopleDetailInfo.facebook.setOnClickListener {
                setOpenURL(s, "facebook")
            }
        } ?: kotlin.run {
            peopleDetailBinding.peopleDetailInfo.facebook.visibility = View.GONE
        }
        data.twitterId?.let { s: String ->
            peopleDetailBinding.peopleDetailInfo.twitter.visibility = View.VISIBLE
            peopleDetailBinding.peopleDetailInfo.twitter.setOnClickListener {
                setOpenURL(s, "twitter")
            }
        } ?: kotlin.run {
            peopleDetailBinding.peopleDetailInfo.twitter.visibility = View.GONE
        }
        data.instagramId?.let { s: String ->
            peopleDetailBinding.peopleDetailInfo.instagram.visibility = View.VISIBLE
            peopleDetailBinding.peopleDetailInfo.instagram.setOnClickListener {
                setOpenURL(s, "instagram")
            }
        } ?: kotlin.run {
            peopleDetailBinding.peopleDetailInfo.instagram.visibility = View.GONE
        }
    }

    override fun setViewCredits(list: MutableList<CreditsItem?>) {
        list.sortWith(presenter.sortYear())
//        Collections.sort(list,presenter.sortYear())
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        peopleDetailBinding.peopleDetailCast.rvActing.layoutManager = layoutManager
        val adapter = PeopleDetailCreditsAdapter(this, list)
        peopleDetailBinding.peopleDetailCast.rvActing.adapter = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter.getItem(position)
                val intent = if (item?.media_type == "tv") {
                    Intent(this@PeopleDetailActivity, TvDetailActivity::class.java)
                } else {
                    Intent(this@PeopleDetailActivity, MovieDetailActivity::class.java)
                }
                item?.id?.let {
                    intent.putExtra(Constant.idMovie, it)
                    startActivity(intent)
                }
            }
        })
    }

    override fun setOpenURL(url: String, status: String) {
        val intent: Intent? = when (status) {
            "facebook" -> {
                Util.startFacebook(this, url)
            }
            "instagram" -> {
                Util.startInstagram(this, url)
            }
            "twitter" -> {
                Util.startTwitter(this, url)
            }
            else -> {
                Intent(Intent.ACTION_VIEW, Uri.parse(url))
            }
        }
        startActivity(intent)
    }

    override fun showDialogNoData(message: String) {
        Util.showRoundedDialog(
            this,
            getString(R.string.no_data),
            message,
            false,
            object : DialogListener {
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
        setSupportActionBar(peopleDetailBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Util.setColorFilter(
            peopleDetailBinding.toolbar.navigationIcon!!,
            ContextCompat.getColor(this, R.color.iconColorPrimary)
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#00FFFFFF")
            window.setFlags(
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES,
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            )
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

    override fun setUI() {
        peopleDetailBinding.btnBackToTop.hide()
        peopleDetailBinding.refreshLayout.setOnRefreshListener {
            peopleDetailBinding.refreshLayout.isRefreshing = false
            presenter.loadData(id)
        }
        peopleDetailBinding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = peopleDetailBinding.scrollView.scrollY
            if (scrollY > 0) {
                peopleDetailBinding.btnBackToTop.show()
            } else {
                peopleDetailBinding.btnBackToTop.hide()
            }
        }
        peopleDetailBinding.btnBackToTop.setOnClickListener {
            peopleDetailBinding.scrollView.smoothScrollTo(0, 0)
        }
    }

    override fun showLoading() {
        peopleDetailBinding.refreshLayout.visibility = View.GONE
        peopleDetailBinding.appbar.visibility = View.GONE
        peopleDetailBinding.shimmerView.visibility = View.VISIBLE
        peopleDetailBinding.shimmerView.setShimmer(
            Shimmer.AlphaHighlightBuilder().setDuration(1150L).build()
        )
        peopleDetailBinding.shimmerView.startShimmer()
    }

    override fun hideLoading() {
        peopleDetailBinding.shimmerView.stopShimmer()
        peopleDetailBinding.shimmerView.visibility = View.GONE
        peopleDetailBinding.refreshLayout.visibility = View.VISIBLE
        peopleDetailBinding.appbar.visibility = View.VISIBLE
    }

}