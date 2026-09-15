package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.VaultDarkBg
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isSetupCompleted: Boolean,
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleAnim = remember { Animatable(0.75f) }
    val alphaAnim = remember { Animatable(0f) }
    val progressAnim = remember { Animatable(0f) }
    var bootStatusText by remember { mutableStateOf("INITIALIZING HARDWARE KEYSTORE...") }

    // Infinite pulsing/rotation for 3D accent
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_orbit")
    val orbitRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween( durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    LaunchedEffect(Unit) {
        // Entrance animation
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )
    }

    LaunchedEffect(Unit) {
        // Step 1
        bootStatusText = "MOUNTING SECURE HARDWARE KEYSTORE..."
        progressAnim.animateTo(0.35f, tween(600, easing = LinearEasing))
        delay(400)

        // Step 2
        bootStatusText = "VERIFYING LOCAL AES-256 CIPHER..."
        progressAnim.animateTo(0.70f, tween(600, easing = LinearEasing))
        delay(400)

        // Step 3
        bootStatusText = if (isSetupCompleted) "AUTHENTICATING JURISDICTIONAL PROFILE..." else "CHECKING LEGAL TENDER ACCORD..."
        progressAnim.animateTo(1.0f, tween(500, easing = LinearEasing))
        delay(300)

        bootStatusText = "VAULT SECURED"
        delay(250)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0F1B2B),
                        VaultDarkBg,
                        Color(0xFF04070D)
                    ),
                    radius = 1200f
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Allow user to tap anywhere to jump immediately
                onSplashFinished()
            }
            .testTag("splash_screen_root"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 3D Solid Icon Bezel with Rotating Holographic Orbit
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(190.dp)
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)
            ) {
                // Background ambient glow halo
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    CyanAccent.copy(alpha = 0.35f * glowPulse),
                                    GoldAccent.copy(alpha = 0.15f * glowPulse),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Rotating cybernetic dashed orbit ring
                Box(
                    modifier = Modifier
                        .size(175.dp)
                        .rotate(orbitRotation)
                        .border(
                            width = 1.5.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    CyanAccent,
                                    Color.Transparent,
                                    GoldAccent,
                                    Color.Transparent,
                                    CyanAccent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Premium 3D Solid Icon Core Card
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = Color(0xFF090E17),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                GoldAccent.copy(alpha = 0.8f),
                                CyanAccent.copy(alpha = 0.6f)
                            )
                        )
                    ),
                    modifier = Modifier
                        .size(130.dp)
                        .shadow(elevation = 20.dp, shape = RoundedCornerShape(32.dp), spotColor = CyanAccent)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_icon_3d),
                        contentDescription = "FinVault 3D Solid Emblem",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(30.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Brand Typography
            Text(
                text = "FINVAULT",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 6.sp,
                fontFamily = FontFamily.SansSerif,
                color = Color.White,
                modifier = Modifier
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)
                    .testTag("splash_brand_title")
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "MILITARY-GRADE PRIVATE FINANCIAL LEDGER",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = CyanAccent.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alphaAnim.value)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Security Badges Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(alphaAnim.value)
            ) {
                SecurityBadge(
                    icon = Icons.Default.Security,
                    label = "AES-256 GCM",
                    tint = CyanAccent
                )
                SecurityBadge(
                    icon = Icons.Default.Lock,
                    label = "100% Offline",
                    tint = GoldAccent
                )
                SecurityBadge(
                    icon = Icons.Default.Gavel,
                    label = "Jurisdiction Sovereign",
                    tint = Color(0xFF10B981)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Glowing Progress Indicator
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .alpha(alphaAnim.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { progressAnim.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .testTag("splash_progress_bar"),
                    color = CyanAccent,
                    trackColor = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = bootStatusText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp,
                    color = Color.LightGray.copy(alpha = 0.7f),
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("splash_boot_status")
                )
            }
        }
    }
}

@Composable
private fun SecurityBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF131E2E).copy(alpha = 0.8f),
        border = androidx.compose.foundation.BorderStroke(1.dp, tint.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}
