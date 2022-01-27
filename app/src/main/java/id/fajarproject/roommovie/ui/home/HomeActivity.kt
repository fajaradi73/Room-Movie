package id.fajarproject.roommovie.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.arlib.floatingsearchview.FloatingSearchView
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.databinding.ActivityHomeBinding
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.ui.base.BaseActivity
import id.fajarproject.roommovie.ui.movie.MovieFragment
import id.fajarproject.roommovie.ui.people.PeopleFragment
import id.fajarproject.roommovie.ui.search.SearchActivity
import id.fajarproject.roommovie.ui.setting.SettingFragment
import id.fajarproject.roommovie.ui.tv.TvFragment
import id.fajarproject.roommovie.util.AppPreference
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Constant.REQUEST_VOICE
import id.fajarproject.roommovie.util.Util
import javax.inject.Inject


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */

class HomeActivity : BaseActivity(), HomeContract.View {

    @Inject
    lateinit var presenter: HomeContract.Presenter

    private lateinit var homeBinding: ActivityHomeBinding

    private val fragmentManager = supportFragmentManager
    private var currentFragment: Fragment? = null
    private var intentStatus = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        injectDependency()
        presenter.attach<Activity>(this, this)
        setToolbar()
        if (isConnection) {
            if (!presenter.checkDataPreferences(Constant.language)) {
                presenter.loadDataLanguage()
            } else {
                if (!presenter.checkDataPreferences(Constant.genreMovie)) {
                    presenter.loadData()
                } else {
                    setUI()
                }
            }
        }
    }

    override fun setUI() {
        homeBinding.navView.setOnItemSelectedListener {
            // do stuff
            when (it.itemId) {
                R.id.action_home -> {
                    addFragment(MovieFragment(), Constant.movie)
                    setHintSearch(getString(R.string.movie))
                    changeToolbar(true)
                    AppPreference.writePreference(
                        this@HomeActivity,
                        Constant.tag,
                        Constant.movie
                    )
                    return@setOnItemSelectedListener true
                }
                R.id.action_tv -> {
                    if (!presenter.checkDataPreferences(Constant.genreTv)) {
                        presenter.loadDataTv()
                    }
                    addFragment(TvFragment(), Constant.tv)
                    setHintSearch(getString(R.string.tv))
                    changeToolbar(true)
                    AppPreference.writePreference(this@HomeActivity, Constant.tag, Constant.tv)
                    return@setOnItemSelectedListener true
                }
                R.id.action_people -> {
                    addFragment(PeopleFragment(), Constant.people)
                    setHintSearch(getString(R.string.people))
                    changeToolbar(true)
                    AppPreference.writePreference(
                        this@HomeActivity,
                        Constant.tag,
                        Constant.people
                    )
                    return@setOnItemSelectedListener true
                }
                R.id.action_setting -> {
                    addFragment(SettingFragment(), Constant.setting)
                    changeToolbar(false)
                    AppPreference.writePreference(
                        this@HomeActivity,
                        Constant.tag,
                        Constant.setting
                    )
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }
        setOpenFragment()
        homeBinding.searchBar.setOnMenuItemClickListener { item ->
            if (item?.itemId == R.id.action_voice) {
                Util.onVoiceClicked(this)
            }
        }
        homeBinding.searchBar.setOnFocusChangeListener(object :
            FloatingSearchView.OnFocusChangeListener {
            override fun onFocusCleared() {
            }

            override fun onFocus() {
                moveToSearch("")
                homeBinding.searchBar.setSearchFocused(false)
            }
        })
    }

    override fun showLoading() {
        homeBinding.loading.visibility = View.VISIBLE
        homeBinding.clContainer.visibility = View.GONE

        window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    override fun hideLoading() {
        homeBinding.loading.visibility = View.GONE
        homeBinding.clContainer.visibility = View.VISIBLE
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .build()

        activityComponent.inject(this)
    }

    override fun setToolbar() {
        setSupportActionBar(homeBinding.toolbar)
    }

    override fun addFragment(fragments: Fragment, tag: String) {

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        var curFrag: Fragment? = null
        if (fragmentManager.primaryNavigationFragment != null) {
            curFrag = fragmentManager.primaryNavigationFragment
        }
        if (curFrag != null) {
            fragmentTransaction.hide(curFrag)
        }

        var fragment: Fragment? =
            fragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            fragment = fragments
            fragmentTransaction.add(homeBinding.container.id, fragment, tag)
        } else {
            if (tag == Constant.setting) {
                fragmentTransaction.detach(fragment).attach(fragment)
                fragmentTransaction.show(fragment)
            } else {
                fragmentTransaction.show(fragment)
            }
        }
        currentFragment = fragment
        fragmentTransaction.setPrimaryNavigationFragment(fragment)
        fragmentTransaction.commit()
    }


    @SuppressLint("DefaultLocale")
    override fun setHintSearch(status: String) {
        intentStatus = status
        homeBinding.searchBar.setSearchHint("${getString(R.string.search_hint)} ${status.lowercase()}")
    }

    override fun moveToSearch(voiceSearch: String) {
        val intent = Intent(this@HomeActivity, SearchActivity::class.java)
        intent.putExtra(Constant.INTENT_STATUS, intentStatus)
        intent.putExtra(Constant.voiceSearch, voiceSearch)
        startActivity(intent)
    }

    override fun changeToolbar(isSearch: Boolean) {
        if (isSearch) {
            homeBinding.searchBar.visibility = View.VISIBLE
            homeBinding.toolbar.visibility = View.INVISIBLE
            title = ""
        } else {
            homeBinding.searchBar.visibility = View.INVISIBLE
            homeBinding.toolbar.visibility = View.VISIBLE
            title = getString(R.string.setting)
        }
    }

    override fun setOpenFragment() {
        when (AppPreference.getStringPreferenceByName(this, Constant.tag)) {
            Constant.tv -> {
                homeBinding.navView.selectedItemId = R.id.action_tv
            }
            Constant.people -> {
                homeBinding.navView.selectedItemId = R.id.action_people
            }
            Constant.setting -> {
                homeBinding.navView.selectedItemId = R.id.action_setting
            }
            else -> {
                homeBinding.navView.selectedItemId = R.id.action_home
            }
        }
    }

    override fun onDestroy() {
        presenter.unsubscribe()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VOICE && resultCode == Activity.RESULT_OK) {
            val matches: ArrayList<String> =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) ?: arrayListOf()
            if (matches.size > 0) {
                val searchWrd = matches[0]
                if (!TextUtils.isEmpty(searchWrd)) {
                    moveToSearch(searchWrd)
                }
            }
            return
        }
    }
}