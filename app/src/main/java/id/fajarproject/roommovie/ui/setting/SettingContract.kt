package id.fajarproject.roommovie.ui.setting


/**
 * Create by Fajar Adi Prasetyo on 22/07/2020.
 */
class SettingContract {
    interface View {
        fun updateFragment()
        fun moveToDetail(status : String)
        fun refreshActivity()
    }
}