package id.fajarproject.roommovie.ui.base

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDelegate
import id.fajarproject.roommovie.ui.widget.DialogListener
import id.fajarproject.roommovie.ui.widget.localeHelper.LocaleAwareCompatActivity
import id.fajarproject.roommovie.util.AppPreference
import id.fajarproject.roommovie.util.Constant
import id.fajarproject.roommovie.util.Util
import io.reactivex.disposables.CompositeDisposable


/**
 * Create by Fajar Adi Prasetyo on 09/07/2020.
 */

open class BaseActivity : LocaleAwareCompatActivity() {
    private val subscriptions = CompositeDisposable()

    var isConnection = true

    protected var activityResult = BaseActivityResult.registerActivityForResult(this)

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        if (AppPreference.getBooleanPreferenceByName(this, Constant.isNightMode)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        if (!Util.isInternetAvailable(this)) {
            showDialogInternet()
            isConnection = false
        }
        super.setContentView(layoutResID)
    }

    override fun onDestroy() {
        super.onDestroy()
        onUnsubscribe()
    }

    private fun onUnsubscribe() {
        subscriptions.clear()

    }

    private fun showDialogInternet() {
        Util.showDialogInternet(this, object : DialogListener {
            override fun onYes() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val panelIntent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
                    activityResult.launch(panelIntent,
                        object : BaseActivityResult.OnActivityResult<ActivityResult> {
                            override fun onActivityResult(result: ActivityResult) {
                                recreate()
                            }
                        })
                } else {
                    activityResult.launch(Intent(Settings.ACTION_WIRELESS_SETTINGS),
                        object : BaseActivityResult.OnActivityResult<ActivityResult> {
                            override fun onActivityResult(result: ActivityResult) {
                                recreate()
                            }
                        })
                }
            }

            override fun onNo() {
                Log.i("BaseActivity","Internet Connection")
            }
        })
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

}