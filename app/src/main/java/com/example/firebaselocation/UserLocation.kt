package com.example.firebaselocation

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import java.io.Serializable
import java.nio.DoubleBuffer

class UserLocation :Serializable{

    var lat:Double = 0.0
    var long:Double = 0.0
    var markerIcon:Int = R.mipmap.marker1
    var userName:String = ""
    var time:Timestamp = Timestamp.now()

    constructor()

}


