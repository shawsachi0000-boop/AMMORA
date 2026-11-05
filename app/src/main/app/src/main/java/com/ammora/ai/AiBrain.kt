package com.ammora.ai

object AiBrain {

    fun reply(question: String): String {
        val q = question.lowercase(LocaleHolder.ROOT)

        return when {
            q.contains("hello") ||
            q.contains("hi") ||
            q.contains("namaste") ||
            q.contains("नमस्ते") ->
                "Hello! Main AMMO hoon. Aaj main aapki kya help kar sakti hoon?"

            q.contains("your name") ||
            q.contains("naam kya") ||
            q.contains("tum kaun") ->
                "Mera naam AMMO hai. Main AMMORA personal assistant hoon."

            q.contains("time") ||
            q.contains("समय") ->
                "Abhi ka time aapke phone ki clock mein dekh sakte hain."

            q.contains("joke") ||
            q.contains("मजाक") ->
                "Teacher: Homework kahan hai? Student: Sir, homework bhi work hai, aur main work from home kar raha tha!"

            q.contains("how are you") ||
            q.contains("kaisi ho") ->
                "Main bilkul ready hoon! Aap bataiye kaise hain?"

            q.contains("thank") ||
            q.contains("धन्यवाद") ->
                "You're welcome! Aapki help karke khushi hui."

            q.contains("bye") ||
            q.contains("अलविदा") ->
                "Bye! Phir milte hain."

            else ->
                "Main abhi basic offline mode mein hoon. Is sawal ka intelligent jawab dene ke liye mujhe online AI connection chahiye."
        }
    }

    private object LocaleHolder {
        val ROOT: java.util.Locale = java.util.Locale.ROOT
    }
}
