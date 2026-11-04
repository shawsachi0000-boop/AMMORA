
package com.ammora.ai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.util.Locale

private data class ChatMessage(
    val isUser: Boolean,
    val message: String
)

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var recognizer: SpeechRecognizer? = null

    private var speechCallback: ((String) -> Unit)? = null
    private var speechErrorCallback: (() -> Unit)? = null

    private val micPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) startVoiceRecognition()
            else speechErrorCallback?.invoke()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this, this)

        setContent {
            AmmoraApp()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {

            val result = tts?.setLanguage(Locale("hi", "IN"))

            tts?.voices
                ?.filter { it.locale.language == "hi" }
                ?.firstOrNull { it.name.contains("female", true) }
                ?.let { tts?.voice = it }
        }
    }

    private fun speak(text: String) {
        tts?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "AMMORA_REPLY"
        )
    }

    private fun startVoiceRecognition() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            micPermission.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            speechErrorCallback?.invoke()
            return
        }

        recognizer?.destroy()

        recognizer = SpeechRecognizer.createSpeechRecognizer(this)

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
                RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "hi-IN"
            )

            putExtra(
                RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                false
            )
        }

        recognizer?.setRecognitionListener(
            object : RecognitionListener {

                override fun onReadyForSpeech(params: Bundle?) {}

                override fun onBeginningOfSpeech() {}

                override fun onRmsChanged(rmsdB: Float) {}

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {}

                override fun onError(error: Int) {
                    speechErrorCallback?.invoke()
                }

                override fun onResults(results: Bundle?) {

                    val result = results
                        ?.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                        )
                        ?.firstOrNull()

                    if (!result.isNullOrBlank()) {
                        speechCallback?.invoke(result)
                    } else {
                        speechErrorCallback?.invoke()
                    }
                }

                override fun onPartialResults(
                    partialResults: Bundle?
                ) {}

                override fun onEvent(
                    eventType: Int,
                    params: Bundle?
                ) {}
            }
        )

        recognizer?.startListening(intent)
    }

    @Composable
    private fun AmmoraApp() {

        var input by remember { mutableStateOf("") }
        var listening by remember { mutableStateOf(false) }

        val messages = remember {
            mutableStateListOf(
                ChatMessage(
                    false,
                    "Hey! 💙 Main AMMORA hoon. Mujhe AMMO bula sakte ho."
                )
            )
        }

        fun sendMessage(text: String) {

            val cleanText = text.trim()

            if (cleanText.isEmpty()) return

            messages.add(
                ChatMessage(true, cleanText)
            )

            val answer = AiBrain.reply(cleanText)

            messages.add(
                ChatMessage(false, answer)
            )

            speak(answer)
            input = ""
        }

        speechCallback = { spoken ->
            listening = false
            input = spoken
            sendMessage(spoken)
        }

        speechErrorCallback = {
            listening = false
            val errorMessage =
                "Sorry, main sun nahi paayi. Dobara try karo."

            messages.add(
                ChatMessage(false, errorMessage)
            )

            speak(errorMessage)
        }

        val permissionLauncher =
            rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->

                if (granted) {
                    listening = true
                    startVoiceRecognition()
                }
            }

        MaterialTheme(
            colorScheme = darkColorScheme(
                primary = Color(0xFF55D6FF),
                background = Color(0xFF070B16),
                surface = Color(0xFF111A2B)
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF080D1C),
                                Color(0xFF10152B),
                                Color(0xFF070B16)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {

                Text(
                    text = "AMMORA",
                    color = Color(0xFF70DFFF),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "YOUR AI COMPANION  •  AMMO",
                    color = Color.LightGray,
                    fontSize = 11.sp
                )

                Spacer(Modifier.height(18.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    Box(
                        modifier = Modifier
                            .size(112.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        Color(0xFF287DAD),
                                        Color(0xFF251B59),
                                        Color.Transparent
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "A",
                            color = Color.White,
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = if (listening)
                        "LISTENING..."
                    else
                        "TALK TO AMMO",

                    color = Color(0xFF70DFFF),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),

                    verticalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    itemsIndexed(messages) { _, item ->

                        Row(
                            modifier = Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                if (item.isUser)
                                    Arrangement.End
                                else
                                    Arrangement.Start
                        ) {

                            Surface(
                                color =
                                    if (item.isUser)
                                        Color(0xFF173A57)
                                    else
                                        Color(0xFF171F35),

                                shape = RoundedCornerShape(16.dp),

                                modifier = Modifier.widthIn(
                                    max = 300.dp
                                )
                            ) {

                                Text(
                                    text = item.message,
                                    color = Color.White,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    OutlinedTextField(
                        value = input,

                        onValueChange = {
                            input = it
                        },

                        modifier = Modifier.weight(1f),

                        placeholder = {
                            Text("AMMO se baat karo...")
                        },

                        singleLine = true,

                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Send
                        ),

                        keyboardActions = KeyboardActions(
                            onSend = {
                                sendMessage(input)
                            }
                        ),

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF55D6FF),
                            unfocusedBorderColor = Color(0xFF46516A)
                        )
                    )

                    IconButton(
                        onClick = {
                            sendMessage(input)
                        }
                    ) {

                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color(0xFF70DFFF)
                        )
                    }

                    IconButton(
                        onClick = {

                            if (
                                ContextCompat.checkSelfPermission(
                                    this@MainActivity,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                listening = true
                                startVoiceRecognition()
                            } else {
                                permissionLauncher.launch(
                                    Manifest.permission.RECORD_AUDIO
                                )
                            }
                        }
                    ) {

                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Microphone",
                            tint = Color(0xFF70DFFF)
                        )
                    }
                }

                Text(
                    text = "AMMORA STARTER VERSION",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }

    override fun onDestroy() {

        recognizer?.destroy()

        tts?.stop()
        tts?.shutdown()

        super.onDestroy()
    }
}
