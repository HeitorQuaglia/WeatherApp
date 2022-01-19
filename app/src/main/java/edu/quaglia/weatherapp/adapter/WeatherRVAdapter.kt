package edu.quaglia.weatherapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.quaglia.weatherapp.R
import edu.quaglia.weatherapp.model.WeatherRV
import java.text.ParseException
import java.text.SimpleDateFormat

class WeatherRVAdapter(
    val context: Context,
    val weatherList: List<WeatherRV>
) : RecyclerView.Adapter<WeatherRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WeatherRVAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherRVAdapter.ViewHolder, position: Int) {

        val weatherRV: WeatherRV = weatherList[position]
        holder.temperatureTV.text = weatherRV.temperature + " ºC"
        Picasso.get().load("http:${weatherRV.icon}").into(holder.conditionIV)
        holder.windTV.text = weatherRV.windSpeed + " Km/h"

        val input = SimpleDateFormat("yyyy-mm-dd hh:mm")
        val output = SimpleDateFormat("hh:mm aa")

        try {
            val date = input.parse(weatherRV.time)
            holder.timeTV.text = output.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val windTV: TextView by lazy { itemView.findViewById(R.id.idTVWindSpeed) }
        val temperatureTV: TextView by lazy { itemView.findViewById(R.id.idTVTemperature) }
        val timeTV: TextView by lazy { itemView.findViewById(R.id.idTVTime) }
        val conditionIV: ImageView by lazy { itemView.findViewById(R.id.idIVCondition) }
    }
}
