package edu.quaglia.weatherapp

import android.Manifest
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
import com.google.android.material.textfield.TextInputEditText
import edu.quaglia.weatherapp.adapter.WeatherRVAdapter
import edu.quaglia.weatherapp.model.WeatherRV
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

        if (checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_CODE)
        }

        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        cityName = getCityName(location!!.latitude, location.longitude)

        getWeatherInfo(cityName!!)

        searchIV.setOnClickListener (object: View.OnClickListener {
            override fun onClick(v: View) {
                val city = cityEdt.text.toString()
                if (city.isEmpty()) {
                    Toast.makeText(this@MainActivity, "Please enter a city name", Toast.LENGTH_SHORT).show()
                } else {
                    cityNameTV.text = cityName
                    getWeatherInfo(city)
                }
            }
        })
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

    private fun getWeatherInfo(cityName: String) {
        val url =
            "http://api.weatherapi.com/v1/current.json?key=$apiKey&q=$cityName&aqi=yes&days=1&alerts=yes"


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
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