
package com.ammora.ai

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AiBrain {

    fun reply(message: String): String {

        val q = message.trim().lowercase(Locale.ROOT)

        if (q.isBlank()) {
            return "Mujhe kuch sunai nahi diya. Dobara bolo."
        }

        return when {

            q.matches(Regex("^(hi|hello|hey|hii).*")) ->
                "Hey! 💙 Main AMMORA hoon. Tum mujhe AMMO bula sakte ho."

            q.contains("your name") ||
            q.contains("naam kya") ||
            q.contains("who are you") ->
                "Mera naam AMMORA hai, short name AMMO."

            q.contains("time") ||
            q.contains("samay") ||
            q.contains("kitne baje") ->
                "Abhi time hai " +
                SimpleDateFormat("hh:mm a", Locale.getDefault())
                    .format(Date())

            q.contains("date") ||
            q.contains("tarikh") ||
            q.contains("aaj kya date") ->
                "Aaj ki date hai " +
                SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                    .format(Date())

            q.contains("how are you") ||
            q.contains("kaisi ho") ->
                "Main bilkul ready hoon! Tum kaise ho? 😊"

            q.contains("joke") ||
            q.contains("mazak") ->
                "Computer doctor ke paas gaya. Doctor bola: tumhe virus hai! 😂"

            q.contains("thank") ||
            q.contains("shukriya") ->
                "You're welcome! Hamesha khushi se help karungi. 💙"

            q.contains("good morning") ->
                "Good morning! Tumhara din bahut achha ho. ☀️"

            q.contains("good night") ->
                "Good night! Sweet dreams. 🌙"

            q.contains("love you") ->
                "Aww! Tum bahut sweet ho. 💙"

            q.contains("help") ||
            q.contains("madad") ->
                "Bolo, kis kaam mein help chahiye?"

            else ->
                "Main tumhari baat sun rahi hoon. 😊 Is starter version mein meri AI knowledge limited hai. Aage real AI brain connect karenge."
        }
    }
}
