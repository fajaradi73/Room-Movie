package id.fajarproject.roommovie.ui.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


/**
 * Create by Fajar Adi Prasetyo on 20/07/2020.
 */

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private var mFragmentList : MutableList<Fragment> = arrayListOf()
    private val mFragmentTitleList: MutableList<String> = arrayListOf()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    fun addFrag(fragment: Fragment,title :String){
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getCount(): Int = mFragmentList.size
    override fun getPageTitle(position: Int): CharSequence? = mFragmentTitleList[position]
}