package com.ait.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.ait.weatherapp.api.WeatherAPI
import com.ait.weatherapp.data.CityWeather
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.detail_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)


        var cityName = intent.getStringExtra(CityListActivity.CITY_NAME)
        val apiid = getString(R.string.api_key)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherApi = retrofit.create(WeatherAPI::class.java)
        var weatherCall = weatherApi.getCityWeather(cityName, "metric", apiid);

        weatherCall.enqueue(object: Callback<CityWeather> {
            override fun onFailure(call: Call<CityWeather>, t: Throwable) {
                Toast.makeText(this@DetailsActivity, t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<CityWeather>, response: Response<CityWeather>) {
                var result = response.body()
                tvName.setText(cityName)
                tvClouds.setText(result!!.clouds.toString())
                tvWind.setText(result!!.wind.toString())
                var tmpMes = result!!.main!!.temp.toString() + " °C"
                tvTemp.setText(tmpMes)
                tvHumid.setText("Humidity: " + result!!.main!!.humidity.toString() + " % ")
                tvTempmin.setText( "Min Temp: " + result!!.main!!.temp_min.toString() + "°C")
                Glide.with(this@DetailsActivity).load(
                    ("https://openweathermap.org/img/w/" + result?.weather?.get(0)?.icon+ ".png")).into(iconWeather)
            }
        })

    }
}
