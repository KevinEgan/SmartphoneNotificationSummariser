package com.example.notificationsummary
import java.io.*
import java.nio.charset.Charset

//
class NotificationFileWrite(private val filePath: String) {



    fun writeToFile(data: NotificationData ){
        val file = File(filePath)
        // TODO: Make this JSON serialisation a bit nicer looking
        val jsonString = """{"capturedAtMs":${data.capturedAtMs},"postTimeMs":${data.postTimeMs},"packageName":"${data.packageName}","title":"${data.title}","text":"${data.text}","bigText":"${data.bigText}","subText":"${data.subText}","category":"${data.category}"}"""
        BufferedWriter(FileWriter(file, true)).use { writer ->
            writer.write(jsonString)
            writer.newLine()
        }
    }
    fun closeFile()
}