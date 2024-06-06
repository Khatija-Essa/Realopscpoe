package com.example.realopsc

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class RecyclerView : AppCompatActivity() {
    companion object {
        private const val VIEWPAGER_COUNT = 4
        private const val PAGE_INDEX_BASIC = 0
        private const val PAGE_INDEX_ADVANCE = 1
        private const val PAGE_INDEX_LANGUAGES = 2
        private const val PAGE_INDEX_CONTACTS = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val view_pager = null
    }

    private class ViewPagerAdapter constructor(
        fm: FragmentManager,
        val mContext: Context?
    ) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        //internal ViewPagerAdapter class, with 3 fragments.
        override fun getItem(position: Int): Fragment {
            return when (position) {
                else -> throw IllegalArgumentException("Not expecting $position.")
            }
        }

        override fun getCount(): Int {
            return VIEWPAGER_COUNT
        }

        // for the pageTitleStrip View at the top of the viewpager
        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                PAGE_INDEX_BASIC -> return mContext?.resources?.getString(R.string.basic_fragment)
                PAGE_INDEX_ADVANCE -> return mContext?.resources?.getString(R.string.advanced_fragment)
                PAGE_INDEX_LANGUAGES -> return mContext?.resources?.getString(R.string.programming_languages_fragment)
                PAGE_INDEX_CONTACTS -> return mContext?.resources?.getString(R.string.contacts_fragment)
            }
            return ""
        }
    }
}