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
import kotlinx.android.synthetic.main.activity_people_detail_info.*
import kotlinx.android.synthetic.main.activity_people_detail.*
import kotlinx.android.synthetic.main.activity_people_detail_cast.*
import kotlinx.android.synthetic.main.activity_people_detail_known_for.*
import kotlinx.android.synthetic.main.activity_people_detail_overview.*
import org.parceler.Parcels
import java.util.*
import javax.inject.Inject

class PeopleDetailActivity : BaseActivity(),PeopleDetailContract.View {

    @Inject lateinit var presenter : PeopleDetailContract.Presenter
    private var id = -1
    private var listKnownForItem : MutableList<KnownForItem?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_people_detail)
        injectDependency()
        presenter.attach(this,this)
        setToolbar()
        setUI()
        id                  = intent.getIntExtra(Constant.idPeople,-1)
        listKnownForItem    = Parcels.unwrap(intent.getParcelableExtra(Constant.dataKnownFor))

        if (id == -1){
            showDialogNoData("")
            return
        }
        if (isConnection){
            presenter.loadData(id)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun showDataSuccess(data: PeopleItem) {
        Glide.with(this)
            .load(Constant.BASE_IMAGE + data.profilePath)
            .placeholder(Util.circleLoading(this))
            .into(ivPoster)
        setOnSetChange(data.name ?: "")

        var biography = ""
        data.biography?.let {
            biography = if (it.isNotEmpty()){
                it
            }else{
                getString(R.string.biographyPeople,data.name)
            }
        } ?: kotlin.run {
            biography = getString(R.string.biographyPeople,data.name)
        }
        tvBiography.text    = biography

        Util.makeTextViewResizable(tvBiography,7,"View More",true)

        tvGender.text       = presenter.getGender(data.gender)
        var birthday        = "-"
        data.birthday?.let {
            birthday  = "${Util.convertDate(it,"yyyy-MM-dd","dd MMMM yyyy")} (${Util.getAge(it,"yyyy-MM-dd")})"
        }
        tvBirthday.text     = birthday
        tvPlaceBirth.text   = data.placeOfBirth
        tvKnownFor.text     = data.knownForDepartment
        tvKnownAs.text      = presenter.getKnowAs(data.alsoKnownAs)
        tvKnownCredits.text = "${presenter.getKnownCredits(data.combinedCredits?.cast).plus(presenter.getKnownCredits(data.combinedCredits?.crew))}"
        acting.text         = data.knownForDepartment

        data.external_ids?.let {
            setViewExternalIDs(it)
        }
        val listCredits : MutableList<CreditsItem?> = arrayListOf()

        if (data.knownForDepartment == "Acting"){
            data.combinedCredits?.cast?.let {
                Collections.sort(it,presenter.sortKnown())
                setViewKnownFor(it)
                listCredits.addAll(it)
            }
        }else{
            data.combinedCredits?.crew?.let {
                Collections.sort(it,presenter.sortKnown())
                setViewKnownFor(presenter.getKnownFor(data.knownForDepartment ?: "",it))
                listCredits.addAll(it)
            }
        }

        Collections.sort(listCredits,presenter.sortYear())
        setViewCredits(listCredits)

        link.setOnClickListener {
            data.homepage?.let {
                if (it.isNotEmpty()) {
                    setOpenURL("https://$it/", "homepage")
                }else{
                    setOpenURL(Constant.BASE_THE_MOVIE_DB,"homepage")
                }
            } ?: kotlin.run {
                setOpenURL(Constant.BASE_THE_MOVIE_DB,"homepage")
            }
        }
    }

    override fun setOnSetChange(name : String){
        appbar.addOnOffsetChangedListener(object : AppBarStateChangeListener(){
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                if (state == State.EXPANDED){
                    tvTitle.text    = name
                    title           = ""
                }else if (state == State.COLLAPSED){
                    tvTitle.text    = ""
                    title           = name
                }
            }
        })
    }
    override fun showDataFailed(message: String) {
        Log.e("ErrorDetail",message)
        showDialogNoData(message)
    }

    override fun setViewKnownFor(list: MutableList<CreditsItem?>) {
        val layoutManager           = LinearLayoutManager(this)
        layoutManager.orientation   = LinearLayoutManager.HORIZONTAL
        rvKnownFor.layoutManager    = layoutManager
        val adapter                 = PeopleDetailKnownForAdapter(this,list)
        rvKnownFor.adapter          = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter.getItem(position)
                val intent = if (item?.media_type == "tv"){
                    Intent(this@PeopleDetailActivity,TvDetailActivity::class.java)
                }else {
                    Intent(this@PeopleDetailActivity,MovieDetailActivity::class.java)
                }
                item?.id?.let {
                    intent.putExtra(Constant.idMovie,it)
                    startActivity(intent)
                }
            }
        })
    }

    override fun setViewExternalIDs(data: ExternalIds) {
        data.facebookId?.let { s: String ->
            facebook.visibility = View.VISIBLE
            facebook.setOnClickListener {
                setOpenURL(s,"facebook")
            }
        } ?: kotlin.run {
            facebook.visibility = View.GONE
        }
        data.twitterId?.let { s: String ->
            twitter.visibility = View.VISIBLE
            twitter.setOnClickListener {
                setOpenURL(s,"twitter")
            }
        } ?: kotlin.run {
            twitter.visibility = View.GONE
        }
        data.instagramId?.let { s: String ->
            instagram.visibility = View.VISIBLE
            instagram.setOnClickListener {
                setOpenURL(s,"instagram")
            }
        } ?: kotlin.run {
            instagram.visibility = View.GONE
        }
    }

    override fun setViewCredits(list: MutableList<CreditsItem?>) {
        val layoutManager           = LinearLayoutManager(this)
        layoutManager.orientation   = LinearLayoutManager.VERTICAL
        rvActing.layoutManager      = layoutManager
        val adapter                 = PeopleDetailCreditsAdapter(this,list)
        rvActing.adapter            = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(view: View?, position: Int) {
                val item = adapter.getItem(position)
                val intent = if (item?.media_type == "tv"){
                    Intent(this@PeopleDetailActivity,TvDetailActivity::class.java)
                }else {
                    Intent(this@PeopleDetailActivity,MovieDetailActivity::class.java)
                }
                item?.id?.let {
                    intent.putExtra(Constant.idMovie,it)
                    startActivity(intent)
                }
            }
        })
    }

    override fun setOpenURL(url: String,status : String) {
        val intent : Intent? = when (status) {
            "facebook" -> {
                Util.startFacebook(this,url)
            }
            "instagram" -> {
                Util.startInstagram(this,url)
            }
            "twitter" ->{
                Util.startTwitter(this,url)
            }
            else -> {
                Intent(Intent.ACTION_VIEW, Uri.parse(url))
            }
        }
        startActivity(intent)
    }

    override fun showDialogNoData(message: String) {
        Util.showRoundedDialog(this,getString(R.string.no_data),"",false,object : DialogListener {
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
        Util.setColorFilter(
            toolbar.navigationIcon!!,
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
        btnBackToTop.hide()
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            presenter.loadData(id)
        }
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = scrollView.scrollY
            if (scrollY > 0){
                btnBackToTop.show()
            }else{
                btnBackToTop.hide()
            }
        }
        btnBackToTop.setOnClickListener {
            scrollView.smoothScrollTo(0,0)
        }
    }

    override fun showLoading() {
        refreshLayout.visibility    = View.GONE
        appbar.visibility           = View.GONE
        shimmerView.visibility  = View.VISIBLE
        shimmerView.setShimmer(Shimmer.AlphaHighlightBuilder().setDuration(1150L).build())
        shimmerView.startShimmer()
    }

    override fun hideLoading() {
        shimmerView.stopShimmer()
        shimmerView.visibility      = View.GONE
        refreshLayout.visibility    = View.VISIBLE
        appbar.visibility           = View.VISIBLE
    }

}