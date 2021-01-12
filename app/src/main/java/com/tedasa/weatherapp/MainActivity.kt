package com.tedasa.weatherapp

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    val location: String = "1642907"
    val api: String = "251395d4269a408655e08de9315a56e9"

    companion object {
        const val TAG = "mainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getWeather().execute()
    }

    inner class getWeather() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainActivity).visibility = View.GONE
            findViewById<TextView>(R.id.error).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response = URL("https://api.openweathermap.org/data/2.5/weather?id=$location&units=metric&appid=$api")
                    .readText(Charsets.UTF_8)
            } catch (e: Exception) {
                response = null
                Log.d(TAG, "Fetch Failed")
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                if (result == null) Log.d(TAG, "Empty response")
                else Log.d(TAG, "Fetch catch")
                /* Parsing JSON */
                val json = JSONObject(result)
                val main = json.getJSONObject("main")
                val sys = json.getJSONObject("sys")
                val wind = json.getJSONObject("wind").getString("speed")
                val weather = json.getJSONArray("weather").getJSONObject(0)
                Log.d(TAG, "JSON Parsed")

                /* Parse time */
                val updated: Long = json.getLong("dt")
                val updatedText =
                    "Updated " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                        Date(updated * 1000)
                    )
                Log.d(TAG, "Time Parsed")

                /* Parse main data */
                val temp = main.getString("temp") + "°C"
                val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
                val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                Log.d(TAG, "Main Parsed")

                /* Parse sys data */
                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val weatherStatus = weather.getString("main")
                val weatherDescription = weather.getString("description")
                Log.d(TAG, "sys Parsed")

                /* Parse address */
                val address = json.getString("name") + ", " + sys.getString("country")
                Log.d(TAG, "address Parsed")

                /* Binding data to view */
                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated).text = updatedText
                findViewById<TextView>(R.id.weather).text = weatherStatus
                findViewById<TextView>(R.id.weatherDescription).text = weatherDescription
                findViewById<TextView>(R.id.temperature).text = temp
                findViewById<TextView>(R.id.min_temperature).text = tempMin
                findViewById<TextView>(R.id.max_temperature).text = tempMax
                findViewById<TextView>(R.id.sunrise_time).text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                        .format(Date(sunrise * 1000))
                findViewById<TextView>(R.id.sunset_time).text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                        .format(Date(sunset * 1000))
                findViewById<TextView>(R.id.wind_speed).text = wind
                findViewById<TextView>(R.id.weather_pressure).text = pressure
                findViewById<TextView>(R.id.weather_humidity).text = humidity

                Log.d(TAG, "View binded")

                /* Hide pre-loader*/
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainActivity).visibility = View.VISIBLE

                Log.d(TAG, "Loader dismiss")

            } catch (e: Exception) {
                Log.d(TAG, "Error catch")
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.error).visibility = View.VISIBLE
            }
        }
    }
}