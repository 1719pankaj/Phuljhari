package com.example.phuljhari

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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

        (activity as AppCompatActivity?)!!.supportActionBar!!.show()



        database = FirebaseDatabase.getInstance().reference

        database.child("test").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
//                val value = dataSnapshot.getValue(String::class.java)
                val map = dataSnapshot.value as Map<*, *>?
                bpm.text = map?.get("bpm").toString()
                updateUi(map, view)
                Log.d("MainActivity", "Value is: $map")
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("MainActivity", "Failed to read value.", error.toException())
            }
        })



        return view
    }

    fun updateUi(map: Map<*, *>?, view: View) {
        count++

        bpm.text = map?.get("BPM").toString() + " bpm"
        view.cpb_bpm.apply {
            progressMax = 100f
            setProgressWithAnimation(map?.get("BPM").toString().toFloat(), 1000)
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
        humidt.text = perHum.toString() + " %"
        val perHumAvg = humTotal / count
        humidt_avg.text = perHumAvg.roundToInt().toString() + " %"
        view.humidt_progress.setProgressPercentage(perHum, true)
        view.avg_humidt_progress.setProgressPercentage(perHumAvg, true)


        val AmbTemp = map?.get("TEMP_AMB_C").toString().toDouble()
        AmbTotal += AmbTemp
        amb_temp.text = AmbTemp.toString() + "°C"
        val PerAmbTemp = (AmbTemp * 100) / 50
        view.amb_temp_progress.setProgressPercentage(PerAmbTemp, true)
        val PerAmbTempAvg = AmbTotal / count
        avg_amb_temp.text = PerAmbTempAvg.roundToInt().toString() + "°C"
        val PerAmbTempAvgPercent = (PerAmbTempAvg * 100) / 50
        view.avg_amb_temp_progress.setProgressPercentage(PerAmbTempAvgPercent, true)

        val heatIndex = map?.get("HT_INDEX_C").toString().toDouble()
        ht_index_c.text = heatIndex.toString() + "°C"
        view.ht_index_progress.setProgressPercentage((heatIndex * 100) / 50, true)
        ht_index_f.text = (heatIndex * 1.8 + 32).roundToInt().toString() + " F"

    }

}