package id.fajarproject.roommovie.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.arlib.floatingsearchview.FloatingSearchView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.di.component.DaggerActivityComponent
import id.fajarproject.roommovie.di.module.ActivityModule
import id.fajarproject.roommovie.models.GenresItem
import id.fajarproject.roommovie.ui.movie.MovieFragment
import id.fajarproject.roommovie.ui.people.PeopleFragment
import id.fajarproject.roommovie.ui.search.SearchActivity
import id.fajarproject.roommovie.ui.setting.SettingFragment
import id.fajarproject.roommovie.ui.tv.TvFragment
import id.fajarproject.roommovie.util.AppPreference
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Constant.REQUEST_VOICE
import id.fajarproject.roommovie.util.Util
import kotlinx.android.synthetic.main.activity_home.*
import java.lang.reflect.Type
import javax.inject.Inject


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */

class HomeActivity : AppCompatActivity() ,HomeContract.View{

    @Inject lateinit var presenter: HomeContract.Presenter

    private val fragmentManager = supportFragmentManager
    private var currentFragment : Fragment? = null
    private var intentStatus = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        injectDependency()
        presenter.attach<Activity>(this,this)
        if (!checkDataPreferences(Constant.language)){
            presenter.loadDataLanguage()
        }else {
            if (!checkDataPreferences(Constant.genreMovie)) {
                presenter.loadData()
            } else {
                setUI()
            }
        }
    }

    override fun setUI() {
        nav_view.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.action_home -> {
                        var fragment = fragmentManager.findFragmentByTag(getString(R.string.movie))
                        if (fragment == null){
                            fragment = MovieFragment()
                        }
                        addFragment(fragment,getString(R.string.movie))
                        setHintSearch(getString(R.string.movie))
                        changeToolbar(true)
                        return true
                    }
                    R.id.action_tv -> {
                        var fragment = fragmentManager.findFragmentByTag(getString(R.string.tv))
                        if (fragment == null){
                            fragment = TvFragment()
                            if (!checkDataPreferences(Constant.genreTv)){
                                presenter.loadDataTv()
                            }
                        }
                        addFragment(fragment,getString(R.string.tv))
                        setHintSearch(getString(R.string.tv))
                        changeToolbar(true)
                        return true
                    }
                    R.id.action_people -> {
                        var fragment = fragmentManager.findFragmentByTag(getString(R.string.people))
                        if (fragment == null){
                            fragment = PeopleFragment()
                        }
                        addFragment(fragment,getString(R.string.people))
                        setHintSearch(getString(R.string.people))
                        changeToolbar(true)
                        return true
                    }
                    R.id.action_setting -> {
                        var fragment = fragmentManager.findFragmentByTag(getString(R.string.setting))
                        if (fragment == null){
                            fragment = SettingFragment()
                        }
                        addFragment(fragment,getString(R.string.setting))
                        changeToolbar(false)
                        return true
                    }
                }
                return false
            }
        })
        nav_view.selectedItemId = R.id.action_home
        searchBar.setOnMenuItemClickListener { item ->
            if (item?.itemId == R.id.action_voice){
                Util.onVoiceClicked(this)
            }
        }
        searchBar.setOnFocusChangeListener(object : FloatingSearchView.OnFocusChangeListener{
            override fun onFocusCleared() {
            }

            override fun onFocus() {
                moveToSearch("")
                searchBar.setSearchFocused(false)
            }
        })
    }

    override fun showLoading() {
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading.visibility = View.GONE
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .build()

        activityComponent.inject(this)
    }

    override fun addFragment(fragments: Fragment, tag: String) {

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        var curFrag: Fragment? = null
        if (fragmentManager.primaryNavigationFragment != null){
            curFrag = fragmentManager.primaryNavigationFragment
        }
        if (curFrag != null) {
            fragmentTransaction.hide(curFrag)
        }

        var fragment: Fragment? =
            fragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            fragment = fragments
            fragmentTransaction.add(container.id, fragment, tag)
        } else {
            if (tag == getString(R.string.setting)){
                fragmentTransaction.detach(fragment).
                attach(fragment)
                fragmentTransaction.show(fragment)
            }else {
                fragmentTransaction.show(fragment)
            }
        }
        currentFragment = fragment
        fragmentTransaction.setPrimaryNavigationFragment(fragment)
        fragmentTransaction.commit()
    }

    override fun saveGenres(list: MutableList<GenresItem?>?,prefName : String) {
        val type: Type? = object : TypeToken<MutableList<GenresItem?>?>() {}.type
        val json: String = Gson().toJson(list, type)
        AppPreference.writePreference(this,prefName,json)
        setUI()
    }

    override fun checkDataPreferences(prefName: String) : Boolean {
        var isNotEmpty = true
        if (AppPreference.getStringPreferenceByName(this,prefName).isNullOrEmpty()){
            isNotEmpty = false
        }
        return isNotEmpty
    }

    @SuppressLint("DefaultLocale")
    override fun setHintSearch(status: String) {
        intentStatus = status
        searchBar.setSearchHint("${getString(R.string.search_hint)} ${status.toLowerCase()}")
    }

    override fun moveToSearch(voiceSearch: String) {
        val intent = Intent(this@HomeActivity,SearchActivity::class.java)
        intent.putExtra(Constant.INTENT_STATUS,intentStatus)
        intent.putExtra(Constant.voiceSearch,voiceSearch)
        startActivity(intent)
    }

    override fun changeToolbar(isSearch: Boolean) {
        if (isSearch){
            searchBar.visibility    = View.VISIBLE
            tvTitle.visibility      = View.GONE
        }else{
            searchBar.visibility    = View.GONE
            tvTitle.visibility      = View.VISIBLE
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