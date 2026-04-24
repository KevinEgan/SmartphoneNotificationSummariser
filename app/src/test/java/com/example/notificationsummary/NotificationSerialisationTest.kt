package com.example.notificationsummary

import org.junit.Test
import org.junit.Assert.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import com.google.gson.Gson

class NotificationSerialisationTest {

    private val gson = Gson()

    @Test
    fun testBasicSerialisation() {
        val data = NotificationData(
            capturedAtMs = 1234567890L,
            postTimeMs = 1234567800L,
            packageName = "com.example.app",
            chatId = "Test Chat",
            title = "Test Title",
            text = "Test Text",
            bigText = "Test Big Text",
            subText = "Test Sub Text",
            category = "msg"
        )

        val json = serialiseNotificationData(data)

        assertNotNull(json)
        assertTrue(json.contains("\"capturedAtMs\":1234567890"))
        assertTrue(json.contains("\"packageName\":\"com.example.app\""))
        assertTrue(json.contains("\"chatId\":\"Test Chat\""))
        assertValidJson(json)
    }

    @Test
    fun testSerialisationAndDeserialisation(){
        val data = NotificationData(
            capturedAtMs = 1234567890L,
            postTimeMs = 1234567800L,
            packageName = "com.example.app",
            chatId = "Test Chat",
            title = "Test Title",
            text = "Test Text",
            bigText = "Test Big Text",
            subText = "Test Sub Text",
            category = "msg"
        )

        val serialisedData = serialiseNotificationData(data)
        val deserialisedData = deserialiseNotificationData(serialisedData)

        assertEquals(data, deserialisedData)
    }

    @Test
    fun testQuotesInStrings() {
        val data = NotificationData(
            capturedAtMs = 1234567890L,
            postTimeMs = 1234567800L,
            packageName = "com.example.app",
            chatId = "Chat \"Quote\"",
            title = "Message from \"John\"",
            text = "He said \"Hello!\"",
            bigText = null,
            subText = null,
            category = null
        )

        val json = serialiseNotificationData(data)

        assertValidJson(json)
        // Gson handles escaping
        assertTrue(json.contains("\\\""))
    }

    @Test
    fun testSpecialChars() {
        val testData = NotificationData(
            capturedAtMs = 1234567890L,
            postTimeMs = 1234567800L,
            packageName = "com.example.app",
            chatId = "Special",
            title = "Line 1\nLine 2",
            text = "Tab\there\nNewline\rCarriage return",
            bigText = "Backslash: \\ Forward slash: /",
            subText = null,
            category = null
        )

        val result = serialiseNotificationData(testData)

        assertValidJson(result)
        assertTrue(result.contains("\\n"))
        // make sure there's no actual newlines in the json
        assertFalse(result.contains("\n\""))
    }

    @Test
    fun testNullFields() {
        val data = NotificationData(
            capturedAtMs = 1234567890L,
            postTimeMs = 1234567800L,
            packageName = "com.example.app",
            chatId = null,
            title = null,
            text = null,
            bigText = null,
            subText = null,
            category = null
        )

        val json = serialiseNotificationData(data)

        assertValidJson(json)
        // Looks as though Gson sorts out null values
        assertFalse(json.contains("\"title\""))
    }

    @Test
    fun testEmptyStrings() {
        val data = NotificationData(
            capturedAtMs = 1234567890L,
            postTimeMs = 1234567800L,
            packageName = "com.example.app",
            chatId = "",
            title = "",
            text = "",
            bigText = "",
            subText = "",
            category = ""
        )

        val json = serialiseNotificationData(data)

        assertValidJson(json)
        assertTrue(json.contains("\"title\":\"\""))
    }

    @Test
    fun unicodeTest() {
        val data = NotificationData(
            capturedAtMs = 1234567890L,
            postTimeMs = 1234567800L,
            packageName = "com.example.app",
            chatId = "Emoji 🦄",
            title = "Hello 👋",
            text = "Test 中文 العربية",
            bigText = "Emoji: 🎉🎊😀",
            subText = null,
            category = null
        )

        val json = serialiseNotificationData(data)

        assertValidJson(json)
        assertNotNull(json)
    }

    @Test
    fun testRoundtripSerialisation() {
        val original = NotificationData(
            capturedAtMs = 1234567890L,
            postTimeMs = 1234567800L,
            packageName = "com.example.app",
            chatId = "Test Group",
            title = "Test",
            text = "Content with \"quotes\" and \nNewlines",
            bigText = null,
            subText = null,
            category = "msg"
        )

        val json = serialiseNotificationData(original)
        val deserialised = deserialiseNotificationData(json)

        assertEquals(original, deserialised)
    }

    private fun assertValidJson(jsonString: String) {
        // basic validation - check braces match up
        assertTrue(jsonString.trim().startsWith("{"))
        assertTrue(jsonString.trim().endsWith("}"))
        
        val openBraces = jsonString.count { it == '{' }
        val closeBraces = jsonString.count { it == '}' }
        assertEquals(openBraces, closeBraces)
    }

    private fun serialiseNotificationData(data: NotificationData): String {
        return gson.toJson(data)
    }

    private fun deserialiseNotificationData(jsonString: String): NotificationData {
        return gson.fromJson(jsonString, NotificationData::class.java)
    }
}
