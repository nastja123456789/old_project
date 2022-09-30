package com.example.partyapp.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.partyapp.R
import com.example.partyapp.currentFragInMain


class MainFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottom_navigation =
            view.findViewById<github.com.st235.lib_expandablebottombar.ExpandableBottomBar>(R.id.bottom_navigation)
        bottom_navigation.select(R.id.ic_active_work)
        if (bottom_navigation != null) {
            val About = AboutFragment()
            val MapScreeen = MapScreenFragment()
            val EventScreen = EventScreenFragment()


            bottom_navigation.onItemSelectedListener = { view, menuItem ->
                when (menuItem.itemId) {
                    R.id.ic_active_work -> {
                        makeCurrentFragmentInMainWindow(About, "activeWorksScreen")
                    }
                    R.id.ic_finish_work -> {
                        makeCurrentFragmentInMainWindow(MapScreeen, "finishWorksScreen")
                    }
                    R.id.ic_user -> {
                        makeCurrentFragmentInMainWindow(EventScreen, "meScreen")
                    }
                }
                true
            }
        }

    }


    private fun makeCurrentFragmentInMainWindow(fragment: Fragment, name: String) {
        currentFragInMain = name
        fragmentManager?.beginTransaction()?.apply {
            replace(R.id.main_fragmnet, fragment)
            addToBackStack(name.toString())
            commit()
        }
    }
}