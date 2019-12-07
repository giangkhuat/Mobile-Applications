package com.ait.weatherapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ait.weatherapp.R
import com.ait.weatherapp.data.city
import kotlinx.android.synthetic.main.city_row.view.*
import androidx.core.content.ContextCompat.startActivity
import com.ait.weatherapp.CityListActivity
import com.ait.weatherapp.DetailsActivity




class weatherAdapter : RecyclerView.Adapter<weatherAdapter.ViewHolder> {

    var cityList  = mutableListOf<city>()
    var context: Context

    companion object {
        const val KEY_DETAILS = "KEY_DETAILS"
        const val REQUEST_DETAILS = 1001
    }

    constructor(context: Context) {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var cityRow = LayoutInflater.from(context).inflate(
            R.layout.city_row, parent, false
        )
        return ViewHolder(cityRow)

    }

    override fun getItemCount(): Int {
      return cityList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      var city = cityList.get(holder.adapterPosition)
        holder.name.text = city.name
        holder.btnDel.setOnClickListener() {
            cityList.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }

        holder.card.setOnClickListener() {
            (context as CityListActivity).showWeatherDetails(holder.name.text.toString())
        }


    }

    fun addCity(city: city) {
        cityList.add(city)
        /* refresh recycler view */
        notifyItemInserted(cityList.lastIndex)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name = itemView.cityName
        var btnDel = itemView.btnDelete
        var card = itemView.cityCard

    }

}