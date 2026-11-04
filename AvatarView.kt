
package com.ammora.ai

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AvatarView(
    isSpeaking: Boolean = false,
    modifier: Modifier = Modifier
) {

    val infiniteTransition = rememberInfiniteTransition(
        label = "avatar_animation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSpeaking) 1.05f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 900,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_pulse"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.62f)
                .scale(pulse)
                .clip(RoundedCornerShape(28.dp))
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0xFF55D6FF),
                            Color(0xFF8A4DFF),
                            Color(0xFF55D6FF)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
        ) {

            Image(
                painter = painterResource(
                    id = R.drawable.ammora_avatar
                ),
                contentDescription = "AMMORA Female Avatar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0xDD070B16)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {

                Text(
                    text = if (isSpeaking)
                        "AMMO IS SPEAKING..."
                    else
                        "AMMO IS LISTENING",
                    color = Color(0xFF70DFFF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(9.dp)
                    .background(
                        if (isSpeaking)
                            Color(0xFF55D6FF)
                        else
                            Color(0xFF42D982),
                        CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (isSpeaking) "Speaking" else "Online",
                color = Color.White,
                fontSize = 13.sp
            )
        }
    }
}
