package com.example.partyapp


import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.partyapp.fragment.*
import kotlinx.android.synthetic.main.activity_main3.*
import pub.devrel.easypermissions.EasyPermissions


var currentFragMain: String? = null
var currentFragInMain: String? = null


class MainActivity : AppCompatActivity() {
    lateinit var loginScreenFragment: LoginScreenFragment
    lateinit var mapScreenFragment: MapScreenFragment
    lateinit var aboutFragment: AboutFragment
    lateinit var eventScreenFragment: EventScreenFragment
    lateinit var contactFragment: ContactFragment
    lateinit var descriptionFragment: DescriptionFragment
    //    lateinit var editFragment: EditFragment
    private val CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        loginScreenFragment = LoginScreenFragment()
        mapScreenFragment = MapScreenFragment()
        aboutFragment = AboutFragment()
        eventScreenFragment = EventScreenFragment()
        descriptionFragment = DescriptionFragment()
        //editFragment = EditFragment()
        contactFragment = ContactFragment()
        currentFragMain = "MainFragment"
        currentFragInMain = "AboutFragment"

        getPermission()

        if (currentFragMain != null) {
            when (currentFragMain) {
                "MainFragment" -> {
                    if (currentFragInMain != null) {
                        when (currentFragInMain) {
                            "MapScreenFragment" -> {
                                makeCurrentFragmentMainWindow(mapScreenFragment, "mainScreen")
                                makeCurrentFragmentInMainWindow(
                                    mapScreenFragment,
                                    "MapScreen"
                                )
                            }
                            "AboutFragment" -> {
                                makeCurrentFragmentMainWindow(aboutFragment, "About")
                                makeCurrentFragmentInMainWindow(
                                    aboutFragment,
                                    "About"
                                )
                            }
                            "EventScreenFragment" -> {
                                makeCurrentFragmentMainWindow(eventScreenFragment, "EventScreen")
                                makeCurrentFragmentInMainWindow(
                                    eventScreenFragment,
                                    "EventScreen"
                                )
                            }
                        }
                    }
                }
            }
        }

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


    private fun makeCurrentFragmentMainWindow(fragment: Fragment, name: String) {
        currentFragMain = name
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_fragmnet, fragment)
            addToBackStack(name.toString())
            commit()
        }
    }

    private fun makeCurrentFragmentInMainWindow(fragment: Fragment, name: String) {
        currentFragInMain = name
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_fragmnet, fragment)
            addToBackStack(name.toString())
            commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState?.run {
            putString("currentFragMain", currentFragMain)
            putString("currentFragInMain", currentFragInMain)
        }
        super.onSaveInstanceState(outState, outPersistentState)
    }

    private fun getPermission() {
        val perms = android.Manifest.permission.ACCESS_FINE_LOCATION

        EasyPermissions.requestPermissions(
            this,
            perms,
            CODE,
            perms
        )
    }


}