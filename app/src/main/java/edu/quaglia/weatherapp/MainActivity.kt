package edu.quaglia.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import edu.quaglia.weatherapp.adapter.WeatherRVAdapter
import edu.quaglia.weatherapp.model.WeatherRV
import org.json.JSONObject
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private val homeRL: RelativeLayout by lazy { findViewById(R.id.idRLHome) }
    private val loadingPB: ProgressBar by lazy { findViewById(R.id.idPBLoading) }
    private val cityNameTV: TextView by lazy { findViewById(R.id.idTVCityName) }
    private val temperatureTV: TextView by lazy { findViewById(R.id.idTVTemperature) }
    private val conditionTV: TextView by lazy { findViewById(R.id.idTVCondition) }
    private val cityEdt: TextInputEditText by lazy { findViewById(R.id.idEdtCity) }
    private val backIV: ImageView by lazy { findViewById(R.id.idIVBack) }
    private val iconIV: ImageView by lazy { findViewById(R.id.idIVIcon) }
    private val searchIV: ImageView by lazy { findViewById(R.id.idIVSearch) }
    private val weatherRV: RecyclerView by lazy { findViewById(R.id.idRVWeather) }
    private val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val PERMISSION_CODE = 1
    private var cityName: String? = null

    private val apiKey = ""

    private val weatherRVList = mutableListOf<WeatherRV>()
    private val weatherRVAdapter = WeatherRVAdapter(this, weatherRVList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_main)
        weatherRV.adapter = weatherRVAdapter

        if (checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_CODE)
        }

        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        cityName = getCityName(location!!.latitude, location.longitude)

        getWeatherInfo(cityName!!)

        searchIV.setOnClickListener {
            val city = cityEdt.text.toString()
            if (city.isEmpty()) {
                Toast.makeText(this@MainActivity,
                    "Please enter a city name",
                    Toast.LENGTH_SHORT).show()
            } else {
                cityNameTV.text = cityName
                getWeatherInfo(city)
            }
        }
    }

    private fun getCityName(lat: Double, lng: Double): String {
        var cityName = "Not found"

        val gcd = Geocoder(baseContext, Locale.getDefault())

        try {
            val addresses = gcd.getFromLocation(lat, lng, 10)

            addresses.forEach {
                it?.let {
                    it.locality?.let { city ->
                        cityName = city
                    } ?: Toast.makeText(this, "User City Not Found", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return cityName
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getWeatherInfo(cityName: String) {
        val url =
            "http://api.weatherapi.com/v1/current.json?key=$apiKey&q=$cityName&aqi=yes&days=1&alerts=yes"

        cityNameTV.text = cityName

        val requestQueue = Volley.newRequestQueue(this@MainActivity)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                loadingPB.visibility = View.GONE
                homeRL.visibility = View.VISIBLE
                weatherRVList.clear()

                val temperature = response?.getJSONObject("current")?.getString("temp_c")
                temperatureTV.text = temperature + " °C"

                val isDay = response?.getJSONObject("current")?.getInt("is_day")

                val condition = response?.getJSONObject("current")?.getJSONObject("condition")
                    ?.getString("text")
                val conditionIcon =
                    response?.getJSONObject("current")?.getJSONObject("condition")
                        ?.getString("icon")

                Picasso.get().load("http:$conditionIcon").into(iconIV)
                conditionTV.text = condition

                val forecast = response?.getJSONObject("forecast")
                val forecast0 = forecast?.getJSONArray("forecastday")?.getJSONObject(0)
                val hour = forecast0?.getJSONArray("hour")

                val length = hour?.length()

                for (i in 0 until length!!) {
                    val hourObj = hour.getJSONObject(i)
                    val time = hourObj.getString("time")
                    val temper = hourObj.getString("temp_c")
                    val img = hourObj.getJSONObject("condition").getString("icon")
                    val wind = hourObj.getString("wind_kph")

                    weatherRVList.add(WeatherRV(
                        time,
                        temper,
                        img,
                        wind
                    ))
                }

                weatherRVAdapter.notifyDataSetChanged()
            }
        ) { error -> Toast.makeText(this@MainActivity, error?.message, Toast.LENGTH_SHORT).show() }

        requestQueue.add(jsonObjectRequest)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    }
}