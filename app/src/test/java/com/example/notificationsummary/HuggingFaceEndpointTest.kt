package com.example.notificationsummary

import org.junit.Test
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.Properties

class HuggingFaceEndpointTest {

    @Test
    fun query() {
        val token = loadTokenFromLocalProperties()
        if (token.isBlank()) {
            println("Skipping: HF_API_TOKEN not set in local.properties")
            return
        }

        val url = "https://router.huggingface.co/hf-inference/models/KevinEgan/flan-t5-small-samsum"
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Authorization", "Bearer $token")
            setRequestProperty("Content-Type", "text/plain")
        }

        try {
            connection.outputStream.write("summarize: Erin: This is a test. I am manifesting that this will be summarised ".toByteArray())
            connection.outputStream.flush()

            val responseBytes = connection.inputStream.readAllBytes()
            val response = String(responseBytes)

            println("Response: $response")
        } finally {
            connection.disconnect()
        }
    }

    private fun loadTokenFromLocalProperties(): String {
        for (file in listOf(File("local.properties"), File("../local.properties"))) {
            if (file.exists()) {
                val props = Properties()
                file.inputStream().use { props.load(it) }
                return props.getProperty("HF_API_TOKEN", "").trim()
            }
        }
        return ""
    }
}




