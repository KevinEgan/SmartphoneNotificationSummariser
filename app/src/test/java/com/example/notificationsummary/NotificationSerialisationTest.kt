package com.example.notificationsummary

import org.junit.Test
import org.junit.Assert.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class NotificationSerialisationTest {

    @Test
    fun testBasicSerialisation() {
        val data = NotificationData(
            capturedAtMs = 1234567890L,
            postTimeMs = 1234567800L,
            packageName = "com.example.app",
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
        assertTrue(json.contains("\"title\":\"Test Title\""))
        assertValidJson(json)
    }

    @Test
    fun testSerialisationAndDeserialisation(){
        val data = NotificationData(
            capturedAtMs = 1234567890L,
            postTimeMs = 1234567800L,
            packageName = "com.example.app",
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
            title = "Message from \"John\"",
            text = "He said \"Hello!\"",
            bigText = null,
            subText = null,
            category = null
        )

        val json = serialiseNotificationData(data)

        assertValidJson(json)
        // quotes need to be escaped or it breaks
        assertTrue(json.contains("\\\""))
    }

    @Test
    fun testSpecialChars() {
        val testData = NotificationData(
            capturedAtMs = 1234567890L,
            postTimeMs = 1234567800L,
            packageName = "com.example.app",
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
            title = null,
            text = null,
            bigText = null,
            subText = null,
            category = null
        )

        val json = serialiseNotificationData(data)

        assertValidJson(json)
        // TODO: check if nulls should be omitted or set to null value
        assertTrue(json.contains("\"title\":null") || !json.contains("\"title\""))
    }

    @Test
    fun testEmptyStrings() {
        val data = NotificationData(
            capturedAtMs = 1234567890L,
            postTimeMs = 1234567800L,
            packageName = "com.example.app",
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
        val dataToJson = Json.encodeToString(data)
        return dataToJson
    }

    private fun deserialiseNotificationData(jsonString: String): NotificationData {
        val dataFromJson = Json.decodeFromString<NotificationData>(jsonString)
        return dataFromJson
    }
}
