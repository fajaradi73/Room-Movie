package id.fajarproject.roommovie.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.ui.home.HomeActivity
import id.fajarproject.roommovie.util.AppPreference
import id.fajarproject.roommovie.util.Constant

/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
class SplashScreen  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppPreference.writePreference(this, Constant.tag,getString(R.string.movie))
        startActivity(Intent(this, HomeActivity::class.java))
        overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )
        finish()
    }
}