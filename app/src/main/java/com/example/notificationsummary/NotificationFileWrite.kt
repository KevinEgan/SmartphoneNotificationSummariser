package com.example.notificationsummary
import java.io.*
import java.nio.charset.Charset
import com.google.gson.Gson

//
class NotificationFileWrite(private val filePath: String) {



    fun writeToFile(data: NotificationData ){
        try {
            val file = File(filePath)
            android.util.Log.d("NotificationFileWrite", "File path: $filePath")
            android.util.Log.d("NotificationFileWrite", "File exists: ${file.exists()}")
            android.util.Log.d("NotificationFileWrite", "Parent dir exists: ${file.parentFile?.exists()}")
            
            val dataToJson = serialiseNotificationData(data)
            android.util.Log.d("NotificationFileWrite", "Serialized data: $dataToJson")
            
            BufferedWriter(FileWriter(file, true)).use { writer ->
                writer.write(dataToJson)
                writer.newLine()
            }
            android.util.Log.d("NotificationFileWrite", "Write completed successfully")
        } catch (error: Exception) {
            android.util.Log.d("NotificationFileWrite", "Error writing to file", error)
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