package com.example.partyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.partyapp.fragment.LoginScreenFragment

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        showFragmentForLoginActivity(supportFragmentManager, LoginScreenFragment())
    }

    private fun showFragmentForLoginActivity(fragmentManager: FragmentManager, fragment: Fragment) {
        currentFragMain = "MainFragment"
        fragmentManager
            .beginTransaction()
            .addToBackStack(fragment.tag.toString())
            .replace(R.id.login_content, fragment, fragment.javaClass.simpleName)
            .commit()
    }
}
