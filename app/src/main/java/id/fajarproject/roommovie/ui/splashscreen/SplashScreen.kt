package id.fajarproject.roommovie.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.ui.home.HomeActivity

/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
class SplashScreen  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, HomeActivity::class.java))
        overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )
        finish()
    }
}