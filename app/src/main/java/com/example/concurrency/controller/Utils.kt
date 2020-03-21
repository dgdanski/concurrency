package com.example.concurrency.controller

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.gson.Gson
import java.math.BigDecimal
import java.math.RoundingMode

class Utils {
    companion object {
        @JvmStatic
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                    ?: return false
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        }

        @JvmStatic
        fun <T> parseJson(json: String?, modelClass: Class<T>?): T {
            return Gson().fromJson(json, modelClass)
        }

        @JvmStatic
        fun round(value: Double, decimalPoint: Int): Double {
            require(decimalPoint >= 0)
            var bigDecimal = BigDecimal.valueOf(value)
            bigDecimal = bigDecimal.setScale(decimalPoint, RoundingMode.HALF_UP)
            return bigDecimal.toDouble()
        }
    }
}