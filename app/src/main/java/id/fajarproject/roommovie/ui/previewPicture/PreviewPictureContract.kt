package id.fajarproject.roommovie.ui.previewPicture


/**
 * Create by Fajar Adi Prasetyo on 12/07/2020.
 */
class PreviewPictureContract {
    interface View {
        fun injectDependency()
        fun setToolbar()
        fun getDataIntent()
        fun setViewPager()
    }
}