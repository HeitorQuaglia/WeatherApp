package edu.quaglia.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}