package com.nicolasght.salut

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var soundPool: SoundPool
    private val soundIds = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = false

        Updater(this).checkForUpdateAsync()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            ).build()
        listOf(R.raw.prout1, R.raw.prout2, R.raw.prout3, R.raw.prout4, R.raw.prout5)
            .forEach { soundIds.add(soundPool.load(this, it, 1)) }

        setContent {
            SalutTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF0F1115)) {
                    var count by remember { mutableStateOf(0) }
                    val rotation = remember { Animatable(0f) }
                    val scale = remember { Animatable(1f) }
                    val scope = rememberCoroutineScope()
                    val interaction = remember { MutableInteractionSource() }

                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(280.dp)
                                .scale(scale.value)
                                .rotate(rotation.value)
                                .clip(CircleShape)
                                .background(Color.Black)
                                .clickable(
                                    interactionSource = interaction,
                                    indication = null
                                ) {
                                    playRandomProut()
                                    count++
                                    scope.launch {
                                        // Shake: rapid alternating rotation
                                        val shakes = listOf(-12f, 12f, -10f, 10f, -7f, 7f, -4f, 4f, 0f)
                                        for (a in shakes) {
                                            rotation.animateTo(
                                                a, animationSpec = tween(45, easing = LinearEasing)
                                            )
                                        }
                                    }
                                    scope.launch {
                                        scale.animateTo(1.12f, tween(80))
                                        scale.animateTo(0.95f, tween(90))
                                        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                    }
                                }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.avatar),
                                contentDescription = "Tap me",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 56.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Tape pour péter 💨",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (count > 0) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "$count prout${if (count > 1) "s" else ""} · v${BuildConfig.VERSION_NAME}",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun playRandomProut() {
        if (soundIds.isEmpty()) return
        val rate = 0.85f + Math.random().toFloat() * 0.4f
        soundPool.play(soundIds.random(), 1f, 1f, 1, 0, rate)
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}
