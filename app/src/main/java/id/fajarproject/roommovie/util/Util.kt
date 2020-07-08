package id.fajarproject.roommovie.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.speech.RecognizerIntent
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.fajarproject.roommovie.BuildConfig
import id.fajarproject.roommovie.R
import id.fajarproject.roommovie.models.GenresItem
import id.fajarproject.roommovie.models.LanguagesItem
import id.fajarproject.roommovie.models.SpokenLanguagesItem
import id.fajarproject.roommovie.ui.widget.DialogListener
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.lang.reflect.Type
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt


/**
 * Create by Fajar Adi Prasetyo on 01/07/2020.
 */
object Util {

    fun getOkHttp(): OkHttpClient {
        val interceptor =
            HttpLoggingInterceptor()
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor).build()
    }

    fun circleLoading(context: Context) : CircularProgressDrawable{
        val circularProgressDrawable            = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth    = 5f
        circularProgressDrawable.centerRadius   = 30f
        circularProgressDrawable.start()
        return circularProgressDrawable
    }
    private var dialog : Dialog? = null

    private fun progressBar(context : Context){
        dialog = Dialog(context)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
    }

    fun showLoading(context: Context){
        progressBar(context)
        if (dialog != null && dialog?.isShowing != true){
            dialog?.show()
            dialog?.setContentView(R.layout.progress)
        }
    }

    fun hideLoading(){
        if(dialog != null && dialog?.isShowing != true){
            dialog?.dismiss()
            dialog?.setContentView(R.layout.progress)
        }
    }
    fun requestFocus(view: View, activity: Activity?) {
        if (view.requestFocus()) {
            activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    private fun capitalize(s: String?): String {
        if (s == null || s.isEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first).toString() + s.substring(1)
        }
    }

    fun setColorFilter(@NonNull drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(
                color,
                BlendMode.SRC_ATOP
            )
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }
    @Suppress("DEPRECATION")
    fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = true
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = true
                    }
                }
            }
        }
        return result
    }
    private var alertDialog : AlertDialog? = null
    private var btnYes : CardView? = null
    private var btnNo : CardView? = null
    private var textYes : TextView? = null
    private fun initShowDialog(activity: Activity,title : String, message : String, isTwoButton : Boolean){
        if (alertDialog != null && alertDialog?.isShowing == true  || activity.isFinishing){
            // A dialog is already open, wait for it to be dismissed, do nothing
            println("alert dialog is not null or alert dialog is showing or context (activity) is finishing")
        }else{
            alertDialog         = AlertDialog.Builder(activity).create()
            val inflater        = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val viewDialog      = inflater.inflate(R.layout.show_dialog,null)
            val titleDialog     = viewDialog.findViewById<TextView>(R.id.title_dialog)
            val messageDialog   = viewDialog.findViewById<TextView>(R.id.message_dialog)
            btnYes              = viewDialog.findViewById(R.id.btn_yes)
            btnNo               = viewDialog.findViewById(R.id.btn_no)
            textYes             = viewDialog.findViewById(R.id.tvYes)
            alertDialog?.setView(viewDialog)
            alertDialog?.setCancelable(false)
            titleDialog.text    = title
            messageDialog.text  = message
            if (message.isNotEmpty()){
                messageDialog.visibility = View.VISIBLE
            }
            if (title.isEmpty()){
                titleDialog.visibility = View.GONE
            }
            if (isTwoButton){
                btnNo?.visibility       = View.VISIBLE
                textYes?.text = activity.resources.getString(R.string.yes)
            }
            alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog?.show()
        }
    }

    fun showRoundedDialog(activity: Activity,title : String,message: String,isTwoButton: Boolean,dialogListener: DialogListener){
        initShowDialog(activity,title,message,isTwoButton)
        btnYes?.setOnClickListener {
            alertDialog?.dismiss()
            dialogListener.onYes()
        }
        btnNo?.setOnClickListener {
            alertDialog?.dismiss()
            dialogListener.onNo()
        }
    }
    fun showRoundedDialog(activity: Activity,title : String,message: String,isTwoButton: Boolean){
        initShowDialog(activity,title,message,isTwoButton)
        btnYes?.setOnClickListener {
            alertDialog?.dismiss()
        }
        btnNo?.setOnClickListener {
            alertDialog?.dismiss()
        }
    }
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
    fun checkDataNull(data: String?): String? {
        return if (data == null || data.isEmpty()) {
            ""
        } else {
            data
        }
    }

    fun convertDate(
        date: String,
        oldFormat: String,
        newFormat: String
    ): String? {
        val newDateFormat =
            SimpleDateFormat(newFormat, Locale.getDefault())
        val oldDateFormat =
            SimpleDateFormat(oldFormat, Locale.getDefault())
        return try {
            newDateFormat.format(oldDateFormat.parse(date)!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }
    fun calculateNoOfColumns(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        return (dpWidth / 180).toInt()
    }

    fun getGenre(context: Context,prefName : String) : MutableList<GenresItem?>?{
        val type: Type? =
            object : TypeToken<MutableList<GenresItem?>?>() {}.type
        return Gson()
            .fromJson(AppPreference.getStringPreferenceByName(context, prefName), type)
    }

    fun onVoiceClicked(context: Activity) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
        ) // setting recognition model, optimized for short phrases – search queries

        intent.putExtra(
            RecognizerIntent.EXTRA_MAX_RESULTS,
            1
        ) // quantity of results we want to receive
        context.startActivityForResult(intent, Constant.REQUEST_VOICE)
    }

    fun initializeCache(context: Context) : Long{
        var size: Long = 0
        size += getDirSize(context.cacheDir)
        if (context.externalCacheDir != null) {
            size += getDirSize(context.externalCacheDir!!)
        }
        return size
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun getDirSize(dir: File): Long {
        var size: Long = 0
        for (file in dir.listFiles()) {
            if (file != null && file.isDirectory) {
                size += getDirSize(file)
            } else if (file != null && file.isFile) {
                size += file.length()
            }
        }
        return size
    }

    fun readableFileSize(size: Long): String? {
        if (size <= 0) return "0 Bytes"
        val units =
            arrayOf("Bytes", "kB", "MB", "GB", "TB")
        val digitGroups =
            (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(
            size / 1024.0.pow(digitGroups.toDouble())
        ).toString() + " " + units[digitGroups]
    }

    fun deleteCache(context: Context) {
        try {
            val dir = context.cacheDir
            deleteDir(dir)
            val ext = context.externalCacheDir
            deleteDir(ext)
            Toast.makeText(context,context.getString(R.string.message_clear_cache),Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }

    fun setViewPercents(context: Context,views : Array<View>){
        for (view in views){
            val width = context.resources.displayMetrics.widthPixels * 3/5
            val layoutParams = view.layoutParams
            layoutParams.width = width
            view.layoutParams = layoutParams
        }
    }
    fun setViewPercents(context: Context,views : Array<View>,percents : Double){
        for (view in views){
            val width = context.resources.displayMetrics.widthPixels * percents
            val layoutParams = view.layoutParams
            layoutParams.width = width.roundToInt()
            view.layoutParams = layoutParams
        }
    }
    fun changeFocusable(views: Array<View>, state: Boolean) {
        for (view in views) {
            view.isFocusable = state
        }
    }

    fun changeVisibility(views: Array<View>, state: Int) {
        for (view in views) {
            view.visibility = state
        }
    }

    fun changeEnabledStatus(views: Array<View>, state: Boolean) {
        for (view in views) {
            view.isEnabled = state
        }
    }
    fun getAppVersion() : String? {
        val tmpVersionName: String = BuildConfig.VERSION_NAME
        val versionCode: Int = BuildConfig.VERSION_CODE
        val versionName = tmpVersionName.substring(0, 3)
        return "$versionName ($versionCode)"
    }
    fun currencyFormatter(num: String): String? {
        val m = num.toDouble()
        val currentLocale = Locale.getDefault()
        val otherSymbols =
            DecimalFormatSymbols(currentLocale)
        otherSymbols.groupingSeparator = ','
        val formatter = DecimalFormat("#,###", otherSymbols)
        return formatter.format(m)
    }

    fun setSpannable(view: TextView,spannable : String,color: Int) {
        val str2 = SpannableString(spannable)
        str2.setSpan(ForegroundColorSpan(color), 0, str2.length, 0)
        view.append(str2)
    }

    fun startInstagram(context: Context, userName: String): Intent? {
        return try {
            // get the Twitter app if possible
            context.packageManager.getPackageInfo("com.instagram.android", 0)
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://instagram.com/_u/$userName")
            )
            //            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } catch (e: java.lang.Exception) {
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://instagram.com/$userName")
            )
        }
    }

    fun startTwitter(context: Context,userName : String) : Intent?{
        return try {
            // get the Twitter app if possible
            context.packageManager.getPackageInfo("com.twitter.android", 0)
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("twitter://user?screen_name=$userName")
            )
        } catch (e: java.lang.Exception) {
            // no Twitter app, revert to browser
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://twitter.com/$userName")
            )
        }
    }

    fun startFacebook(context: Context,facebook: String) :Intent?{
        val intent = Intent(Intent.ACTION_VIEW)
        try {
            context.packageManager.getPackageInfo("com.facebook.katana", 0)
            val versionCode: Int =
                context.packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
            if (versionCode >= 3002850) { //newer versions of fb app
                intent.data = Uri.parse("fb://facewebmodal/f?href=${Constant.URL_FACEBOOK}${facebook}")
            } else { //older versions of fb app
                intent.data = Uri.parse("fb://page/$facebook")
            }
            intent.setPackage("com.facebook.katana")
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        } catch (e: java.lang.Exception) {
            intent.data = Uri.parse("https://facebook.com/$facebook")
        }
        return intent
    }

    fun getLanguage(string: String?,context: Context) : String{
        var language = ""
        val type: Type? = object : TypeToken<MutableList<LanguagesItem?>?>() {}.type
        val list :MutableList<LanguagesItem?>? = Gson().fromJson(AppPreference.getStringPreferenceByName(context, Constant.language), type)
        if (list != null) {
            for (data in list){
                if (data?.iso6391 == string){
                    language = data?.englishName ?: ""
                    break
                }
            }
        }
        return language
    }
}