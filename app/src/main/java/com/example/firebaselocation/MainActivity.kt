package com.example.firebaselocation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebaselocation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    companion object{
        const val USER_NAME = "user_name"
        const val MARKER_ICON = "marker_icon"
    }

    private lateinit var binding: ActivityMainBinding

    private var markerIcon:Int = R.mipmap.marker1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnStart.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra(USER_NAME, binding.etUserName.text.toString().trim())
            intent.putExtra(MARKER_ICON, markerIcon)
            startActivity(intent)
        }

        binding.ivMarker1.setOnClickListener { markerIcon = R.mipmap.marker1 }
        binding.ivMarker2.setOnClickListener { markerIcon = R.mipmap.marker2 }
        binding.ivMarker3.setOnClickListener { markerIcon = R.mipmap.marker3 }

    }
}