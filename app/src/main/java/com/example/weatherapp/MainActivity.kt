package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Query
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


//https://api.openweathermap.org/data/2.5/

//88d1387d2af33e58c91e7a96b36cd522
//{"coord":{"lon":77.2167,"lat":28.6667},"weather":[{"id":721,"main":"Haze","description":"haze","icon":"50n"}],"base":"stations","main":{"temp":293.21,"feels_like":292.94,"temp_min":293.2,"temp_max":293.21,"pressure":1011,"humidity":64},"visibility":2500,"wind":{"speed":4.12,"deg":120},"clouds":{"all":75},"dt":1709322506,"sys":{"type":2,"id":145989,"country":"IN","sunrise":1709342116,"sunset":1709383895},"timezone":19800,"id":1273294,"name":"Delhi","cod":200}

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding =ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherdata("jaipur")
        searchcity()
    }

    private fun searchcity() {
        val searchview = binding.searchView
        searchview.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherdata(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })


    }

    private fun fetchWeatherdata(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(Apiinterface::class.java)
        val response = retrofit.getweatherdata(cityName , "88d1387d2af33e58c91e7a96b36cd522", "metric")
        response.enqueue(object : Callback<Weatherapp> {
            override fun onResponse(call: Call<Weatherapp>, response: Response<Weatherapp>) {
                  val responseBody = response.body()
                 if(response.isSuccessful && responseBody!=null){
                     val temperature = responseBody.main.temp
                     val humidity = responseBody.main.humidity
                     val windspeed = responseBody.wind.speed
                     val sunRise = responseBody.sys.sunrise.toLong()
                     val sunset = responseBody.sys.sunset.toLong()
                     val sealevel =responseBody.main.pressure
                     val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                     val maxtemp = responseBody.main.temp_max
                     val mintemp = responseBody.main.temp_min
                     Log.d("tag","onResponse:$temperature")
                     binding.tvtempmain.text = "$temperature °C"
                     binding.maxtemp.text = "max temp : $maxtemp °C "
                     binding.mintemp.text="min temp : $mintemp °C"
                     binding.tvcardkeandar11.text="$humidity %"
                     binding.tvcardkeandar21.text="$windspeed m/s"
                     binding.tvcardkeandar31.text="${time(sunRise)}"
                     binding.tvcardkeandar41.text ="${time(sunset)}"
                     binding.tvcardkeandar51.text="$sealevel hpa "
                     binding.tvcardkeandar61.text=condition
                     binding.maincondition.text=condition
                     binding.day.text = dayName(System.currentTimeMillis())
                         binding.date.text = date()
                         binding.cityname.text ="$cityName"
                     
                     changeimageaccordingtocondition(condition)

                 }
            }

            private fun changeimageaccordingtocondition(conditions: String) {
                when(conditions){
                    "Clearsky","Sunny","Clear"->{
                        binding.root.setBackgroundResource(R.drawable.sunny1)
                        binding.lottieAnimationView.setAnimation(R.raw.sun)
                    }

                   "Haze", "Partly Cloud","Clouds","Overcast","Mist","Foggy"->{
                        binding.root.setBackgroundResource((R.drawable.cloudy))
                        binding.lottieAnimationView.setAnimation(R.raw.hazy)
                    }

                    "Light Rain","Rainy","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                        binding.root.setBackgroundResource((R.drawable.rainy))
                        binding.lottieAnimationView.setAnimation(R.raw.rainyone)
                    }
                    else->{
                        binding.root.setBackgroundResource(R.drawable.sunny1)
                        binding.lottieAnimationView.setAnimation(R.raw.sun)
                    }
                }
             binding.lottieAnimationView.playAnimation()

            }

            override fun onFailure(call: Call<Weatherapp>, t: Throwable) {

            }
        })
    }

    private fun date():String{
        val sdf = SimpleDateFormat("dd MMMM YYYY", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timestamp:Long):String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
    fun dayName (timestamp:Long):String{
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}
