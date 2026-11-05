package com.ammora.ai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.util.Locale

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
            AmmoraApp(
                onSpeak = { text ->
                    tts?.speak(
                        text,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "AMMO_REPLY"
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
fun AmmoraApp(onSpeak: (String) -> Unit) {

    val context = LocalContext.current

    var message by remember { mutableStateOf("") }
    var pendingMic by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            "AMMO: Hello Sachin! Main AMMO hoon. Aapki kya help kar sakti hoon?"
        )
    }

    val speechLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            val spokenText = result.data
                ?.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                ?.firstOrNull()

            if (!spokenText.isNullOrBlank()) {
                message = spokenText
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted && pendingMic) {
                pendingMic = false

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
                    putExtra(
                        RecognizerIntent.EXTRA_PROMPT,
                        "AMMO ko boliye"
                    )
                }

                try {
                    speechLauncher.launch(intent)
                } catch (_: Exception) {
                    messages.add(
                        "AMMO: Speech recognition available nahi hai."
                    )
                }
            } else {
                pendingMic = false
                messages.add(
                    "AMMO: Microphone permission nahi mili."
                )
            }
        }

    fun startListening() {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
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
                putExtra(
                    RecognizerIntent.EXTRA_PROMPT,
                    "AMMO ko boliye"
                )
            }

            try {
                speechLauncher.launch(intent)
            } catch (_: Exception) {
                messages.add(
                    "AMMO: Speech recognition available nahi hai."
                )
            }
        } else {
            pendingMic = true
            permissionLauncher.launch(
                Manifest.permission.RECORD_AUDIO
            )
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF080D18)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
        ) {

            Text(
                text = "AMMORA",
                color = Color(0xFF36D9FF),
                fontSize = 30.sp
            )

            Text(
                text = "YOUR PERSONAL AI ASSISTANT",
                color = Color.LightGray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "HELLO, I'M AMMO",
                color = Color.White,
                fontSize = 23.sp
            )

            Text(
                text = "Your assistant is ready.",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { item ->

                    val isAmmo = item.startsWith("AMMO:")

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAmmo) {
                                Color(0xFF172B40)
                            } else {
                                Color(0xFF263047)
                            }
                        )
                    ) {
                        Text(
                            text = item,
                            color = Color.White,
                            modifier = Modifier.padding(14.dp),
                            fontSize = 15.sp
                        )
                    }
                }
            }

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Message AMMO...")
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF36D9FF),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFF36D9FF)
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Button(
                    onClick = {
                        val question = message.trim()

                        if (question.isNotEmpty()) {
                            messages.add("You: $question")

                            val answer = AiBrain.reply(question)

                            messages.add("AMMO: $answer")
                            onSpeak(answer)

                            message = ""
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF087FA8)
                    )
                ) {
                    Text("SEND")
                }

                Button(
                    onClick = { startListening() },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF243C59)
                    )
                ) {
                    Text("🎙 MIC")
                }
            }
        }
    }
}
