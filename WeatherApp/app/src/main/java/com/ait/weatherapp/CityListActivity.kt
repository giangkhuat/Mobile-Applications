package com.ait.weatherapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.ait.weatherapp.adapter.weatherAdapter
import com.ait.weatherapp.data.city
import kotlinx.android.synthetic.main.activity_city_list.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class CityListActivity : AppCompatActivity() {

    lateinit var  adapter: weatherAdapter
    companion object {
        const val CITY_NAME = "CITY_NAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_list)
        setSupportActionBar(toolbar)

        adapter = weatherAdapter(this)
        recycler.adapter = adapter
        fab.setOnClickListener {
                showAddDialog()
        }
    }

    private fun showAddDialog() {

        val dialog: MaterialDialog = MaterialDialog(this).show {
            title(R.string.message)
            cornerRadius(16f)
            input { dialog, text ->
                // Text submitted with the action button
                if (!text.toString().equals("")) {
                    val city = city(text.toString(), "")
                    adapter.addCity(city)
                }
            }
            positiveButton(R.string.btn_submit)
            negativeButton(text = "Cancel")
        }
        dialog.show()
        /*
        val dialog: MaterialDialog = MaterialDialog(this)
        dialog.show {
        //icon(R.drawable.icon)
        cornerRadius(16f)
        title(R.string.message)
        //val dialog: MaterialDialog = MaterialDialog()
        val inputField: EditText = dialog.getInputField()
        input() { dialog, text ->
            if (!inputField.text.toString().equals("")) {
                val city = city(inputField.text.toString(), "")
                adapter.addCity(city)
            }
        }
        positiveButton(R.string.btn_submit)
        negativeButton(text = "Cancel")

         */

    }

    fun showWeatherDetails(cityName: String) {
        var intentDetails = Intent()
        intentDetails.setClass(this@CityListActivity,
            DetailsActivity::class.java)
        intentDetails.putExtra(CITY_NAME, cityName)
        startActivity(intentDetails)

    }
}
