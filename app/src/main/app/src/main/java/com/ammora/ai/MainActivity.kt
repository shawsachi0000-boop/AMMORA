
package com.ammora.ai

import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.speech.tts.TextToSpeech
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import java.util.Locale

data class ChatMessage(
    val message: String,
    val isUser: Boolean
)

class MainActivity : ComponentActivity() {

    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("hi", "IN")
            }
        }

        setContent {
            AmmoraScreen(
                onSpeak = { text ->
                    tts?.speak(
                        text,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "AMMORA_REPLY"
                    )
                }
            )
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}

@Composable
fun AmmoraScreen(
    onSpeak: (String) -> Unit
) {
    var input by remember { mutableStateOf("") }
    var listening by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                "Hello! 💙 Main AMMORA hoon. Tum mujhe AMMO bula sakte ho.",
                false
            )
        )
    }

    val speechLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            val spoken = result.data
                ?.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )?.firstOrNull()

            if (!spoken.isNullOrBlank()) {
                input = spoken
                listening = false
            } else {
                listening = false
            }
        }

    fun sendMessage() {
        val question = input.trim()

        if (question.isEmpty()) return

        messages.add(ChatMessage(question, true))

        val answer = AiBrain.reply(question)

        messages.add(ChatMessage(answer, false))

        onSpeak(answer)
        input = ""
    }

    val background = Brush.verticalGradient(
        listOf(
            Color(0xFF050817),
            Color(0xFF101B36),
            Color(0xFF070B19)
        )
    )

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "AMMORA",
                        color = Color.Cyan,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        "YOUR PERSONAL AI",
                        color = Color.LightGray,
                        fontSize = 10.sp
                    )
                }

                Spacer(Modifier.weight(1f))

                Text(
                    "● ONLINE",
                    color = Color(0xFF42F5B0),
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .border(
                        1.dp,
                        Color.Cyan.copy(alpha = 0.7f),
                        RoundedCornerShape(24.dp)
                    )
                    .background(
                        Color(0xFF101B36),
                        RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "AMMO",
                        color = Color.Cyan,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "I'm listening to you 💙",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "CONVERSATION",
                color = Color.Cyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { item ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            if (item.isUser)
                                Arrangement.End
                            else
                                Arrangement.Start
                    ) {
                        Text(
                            text = item.message,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .widthIn(max = 290.dp)
                                .background(
                                    if (item.isUser)
                                        Color(0xFF174D83)
                                    else
                                        Color(0xFF202B45),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(13.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFF18243D),
                        CircleShape
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                BasicTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 14.sp
                    ),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box {
                            if (input.isEmpty()) {
                                Text(
                                    "Ask AMMO anything...",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                TextButton(
                    onClick = {
                        listening = true

                        val intent = Intent(
                            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
                        ).apply {
                            putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                            )
                            putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE,
                                "hi-IN"
                            )
                        }

                        speechLauncher.launch(intent)
                    }
                ) {
                    Text("🎙", fontSize = 20.sp)
                }

                Button(
                    onClick = { sendMessage() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF087EE5)
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 14.dp,
                        vertical = 10.dp
                    )
                ) {
                    Text("SEND")
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                if (listening) "Listening..." else "AMMORA AI • Version 1.0",
                color = Color.Gray,
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
