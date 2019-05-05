package com.training.droidbountyhunter.ui.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.training.droidbountyhunter.R
import com.training.fragments.AcercaDeFragment
import com.training.fragments.ListFragment
import com.training.fragments.SECTION_NUMBER

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager,val fragments:  ArrayList<Fragment>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        if (fragments.size < 3){ // Si no contiene los 3 fragments los agregará
            if (position < 2){
                fragments.add(position, ListFragment())
                val arguments = Bundle()
                arguments.putInt(SECTION_NUMBER, position)
                fragments[position].arguments = arguments
            }else{
                fragments.add(position, AcercaDeFragment())
            }
        }
        return fragments[position]
    }

    override fun getPageTitle(position: Int ) = when (position) {
        0 -> context.getString(R.string.titulo_fugitivos).toUpperCase()
        1 -> context.getString(R.string.titulo_capturados).toUpperCase()
        else -> context.getString(R.string.titulo_acerca_de).toUpperCase()
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 3
    }
}