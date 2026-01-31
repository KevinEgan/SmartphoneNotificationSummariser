package com.example.notificationsummary
import java.io.*
import java.nio.charset.Charset
import com.google.gson.Gson

//
class NotificationFileWrite(private val filePath: String) {



    fun writeToFile(data: NotificationData ){
        val file = File(filePath)
        val dataToJson = serialiseNotificationData(data)
        BufferedWriter(FileWriter(file, true)).use { writer ->
            writer.write(dataToJson)
            writer.newLine()
        }
    }
    fun closeFile(){
        //I believe BufferedWriter takes care of this for me. Leaving this in as a placeholder for now just in case
    }

    private fun serialiseNotificationData(data: NotificationData): String{
        val gson = Gson()
        val json = gson.toJson(data)
        return json
    }
    private fun deserialiseNotificationData(json: String): NotificationData{
        val gson = Gson()
        val deserialised = gson.fromJson(json, NotificationData::class.java)
        return deserialised
    }
}