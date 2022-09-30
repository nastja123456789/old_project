package com.example.partyapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.partyapp.R
import com.google.android.material.button.MaterialButton

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val buttonReset = view.findViewById<MaterialButton>(R.id.registrationButtonFrag1)
        buttonReset.setOnClickListener() {
            val contactFragment = ContactFragment()
            val bundle = Bundle()
            contactFragment.arguments = bundle
            makeCurrentFragmentInMainWindow(contactFragment, "descriptionFragment")
        }
    }
    private fun makeCurrentFragmentInMainWindow(fragment: Fragment, name: String) {
        fragmentManager?.beginTransaction()?.apply {
            replace(R.id.main_fragment, fragment)
            addToBackStack(name.toString())
            commit()
        }
    }
}