package bjzhou.coolapk.app.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bjzhou.coolapk.app.R
import bjzhou.coolapk.app.ui.fragments.PicturesFragment
import kotlinx.android.synthetic.main.fragment_pictures_root.*

class PicturesRootFragment : Fragment() {

    private lateinit var mSectionsPagerAdapter: SectionsPagerAdapter

    var onSetupTabs: ((ViewPager) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pictures_root, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        mSectionsPagerAdapter = SectionsPagerAdapter(fragmentManager)
        picturesPager.adapter = mSectionsPagerAdapter
        onSetupTabs?.invoke(picturesPager)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return PicturesFragment.newInstance(position)
        }

        override fun getCount(): Int {
            return 5
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return getString(R.string.action_recommended)
                1 -> return getString(R.string.action_hot)
                2 -> return getString(R.string.action_start_page)
                3 -> return getString(R.string.action_newest)
                4 -> return getString(R.string.action_2k)
            }
            return null
        }
    }

    companion object {
        fun newInstance(): PicturesRootFragment {
            return PicturesRootFragment()
        }
    }
}
