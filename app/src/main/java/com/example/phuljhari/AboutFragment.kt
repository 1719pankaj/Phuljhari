package com.example.phuljhari

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_about.view.*


class AboutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        val nightModeFlags = requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> view.imageView.setImageDrawable(resources.getDrawable(R.drawable.github_white))
            Configuration.UI_MODE_NIGHT_NO -> view.imageView.setImageDrawable(resources.getDrawable(R.drawable.github_logo))
            Configuration.UI_MODE_NIGHT_UNDEFINED -> view.imageView.setImageDrawable(resources.getDrawable(R.drawable.github_logo))
        }

        view.imageView.setOnClickListener{
            openGithub()
        }





        return view
    }
    //open a browser to the github page.
    fun openGithub() {
        val url = "https://github.com/1719pankaj/Phuljhari"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

}