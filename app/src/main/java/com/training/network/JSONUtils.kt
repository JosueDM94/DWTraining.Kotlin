package com.training.network

import android.content.Context
import com.training.data.*
import com.training.models.*
import org.json.JSONArray
import org.json.JSONException

class JSONUtils {
    companion object {
        fun parsearFugitivos(respuesta: String, context: Context): Boolean {
            val database = DatabaseBountyHunter(context)
            try {
                val array = JSONArray(respuesta)
                for (i in 0 until array.length()) {
                    val objecto = array.getJSONObject(i)
                    val nombreFugitivo = objecto.optString("name", "")
                    database.insertarFugitivo(Fugitivo(0, nombreFugitivo, 0))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                return false
            } finally {
                return true
            }
        }
    }
}