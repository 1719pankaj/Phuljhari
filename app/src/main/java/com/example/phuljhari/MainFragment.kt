package com.example.phuljhari

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import kotlin.math.roundToInt


class MainFragment : Fragment() {

    private lateinit var database: DatabaseReference



    var count = 0
    var humTotal = 0.0
    var AmbTotal = 0.0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

//        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        setTint(view)

        database = FirebaseDatabase.getInstance().reference

        var map: Map<*, *>? = null

        val myListener = database.child("test").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                map = dataSnapshot.value as Map<*, *>?
                updateUi(map, view)
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("MainActivity", "Failed to read value.", error.toException())
            }
        })

        view.swiperefresh.setOnRefreshListener {
            updateUi(map, view)
            setTint(view)
            Handler().postDelayed({ swiperefresh.isRefreshing = false }, 600)
        }

        view.fab_about.setOnClickListener {

            //Leme explain this to you, when you navigate to the about fragment,
            //The Firebase value event listener will keep listening to the database for updates
            //And it will keep calling the updateUi function, which will try to update the UI.
            //But remember how we gotta pass a the fragment as view to the method or it says null object blah blah bs.
            //Imagine the view doesn't exist, so it will crash.
            //That is why we remove the listener when we navigate to the about fragment.
            //And it gets added again when we navigate back to the main fragment.
            //Yes this is painful, yes I want to kill myself.
            //Good bye.
            database.child("test").removeEventListener(myListener)

            findNavController().navigate(R.id.action_mainFragment_to_aboutFragment)
        }

        return view
    }

    //Get the color from the theme
    @ColorInt
    private fun getThemeColor(@AttrRes attrColor: Int): Int {
        val typedValue = TypedValue()
        val a = requireActivity().theme
        a.resolveAttribute(attrColor, typedValue, true)
        return typedValue.data
    }


    fun setTint(view: View): Boolean {

        view.humidt_progress.setProgressDrawableColor(getThemeColor(com.google.android.material.R.attr.colorSecondaryVariant))
        view.humidt_progress.setBackgroundDrawableColor(getThemeColor(com.google.android.material.R.attr.colorPrimaryVariant))

        view.amb_temp_progress.setProgressDrawableColor(getThemeColor(com.google.android.material.R.attr.colorSecondaryVariant))
        view.amb_temp_progress.setBackgroundDrawableColor(getThemeColor(com.google.android.material.R.attr.colorPrimaryVariant))

        view.avg_humidt_progress.setProgressDrawableColor(getThemeColor(com.google.android.material.R.attr.colorOnPrimary))
        view.avg_humidt_progress.setBackgroundDrawableColor(getThemeColor(com.google.android.material.R.attr.colorPrimaryVariant))

        view.avg_amb_temp_progress.setProgressDrawableColor(getThemeColor(com.google.android.material.R.attr.colorOnPrimary))
        view.avg_amb_temp_progress.setBackgroundDrawableColor(getThemeColor(com.google.android.material.R.attr.colorPrimaryVariant))

        view.ht_index_progress.setProgressDrawableColor(getThemeColor(com.google.android.material.R.attr.colorOnPrimary))
        view.ht_index_progress.setBackgroundDrawableColor(getThemeColor(com.google.android.material.R.attr.colorPrimaryVariant))

//        view.cpb_bpm.progressBarColor = getThemeColor(com.google.android.material.R.attr.colorPrimaryVariant)
//        view.cpb_bpm.backgroundProgressBarColor = R.color.least_accent_night
//
//        view.cpb_bpm.progressBarColor = getThemeColor(com.google.android.material.R.attr.colorPrimaryVariant)
//        view.cpb_bpm.backgroundProgressBarColor = R.color.less_accent_night
//
//        view.cpb_bpm.progressBarColor = getThemeColor(com.google.android.material.R.attr.colorPrimaryVariant)
//        view.cpb_bpm.backgroundProgressBarColor = R.color.accent_night

        return true
    }

    @SuppressLint("SetTextI18n")
    fun updateUi(map: Map<*, *>?, view: View) {
        count++

        val bpmTmp = map?.get("BPM").toString()
        bpm.text = "$bpmTmp bpm"
        view.cpb_bpm.apply {
            progressMax = 100f
            setProgressWithAnimation(bpmTmp.toString().toFloat(), 1000)
        }

        spo2.text = map?.get("SPO2").toString() + " %"
        view.cpb_spo2.apply {
            progressMax = 105f
            val spo2 = map?.get("SPO2").toString().toFloat()
            if (spo2 == 100f) {
                setProgressWithAnimation(105f, 1000)
            } else {
                setProgressWithAnimation(spo2, 1000)
            }
        }

        tCont.text = map?.get("TEMP_CONT_C").toString() + "°C"
        view.cpb_tCont.apply {
            progressMax = 50f
            setProgressWithAnimation(map?.get("TEMP_CONT_C").toString().toFloat(), 1000)
        }

        val perHum = map?.get("HUMIDT").toString().toDouble()
        humTotal += perHum
        humidt.text = "$perHum %"
        val perHumAvg = humTotal / count
        humidt_avg.text = "Average ~ " + perHumAvg.roundToInt().toString() + " %"
        view.humidt_progress.setProgressPercentage(perHum, true)
        view.avg_humidt_progress.setProgressPercentage(perHumAvg, true)


        val AmbTemp = map?.get("TEMP_AMB_C").toString().toDouble()
        AmbTotal += AmbTemp
        amb_temp.text = "$AmbTemp°C"
        val PerAmbTemp = (AmbTemp * 100) / 50
        view.amb_temp_progress.setProgressPercentage(PerAmbTemp, true)
        val PerAmbTempAvg = AmbTotal / count
        avg_amb_temp.text = "Average ~ " + PerAmbTempAvg.roundToInt().toString() + "°C"
        val PerAmbTempAvgPercent = (PerAmbTempAvg * 100) / 50
        view.avg_amb_temp_progress.setProgressPercentage(PerAmbTempAvgPercent, true)

        val heatIndex = map?.get("HT_INDEX_C").toString().toDouble()
        ht_index_c.text = "$heatIndex°C"
        view.ht_index_progress.setProgressPercentage((heatIndex * 100) / 60, true)
        ht_index_f.text = map?.get("HT_INDEX_F").toString() + " F"

    }

}