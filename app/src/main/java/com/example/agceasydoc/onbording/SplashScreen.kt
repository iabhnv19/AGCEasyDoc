package com.example.agceasydoc.onbording

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.agceasydoc.R
import com.example.agceasydoc.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController
) {

    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 2200,
                easing = FastOutSlowInEasing
            )
        )

        delay(300)

        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .align(Alignment.Center)
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 32.dp,
                    vertical = 28.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.agc_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(178.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "EasyDoc",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF111111).copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                LinearProgressIndicator(
                    progress = progress.value,
                    modifier = Modifier
                        .width(180.dp)
                        .height(7.dp),
                    color = Color(0xFF3D57FF),
                    trackColor = Color(0xFF111111).copy(alpha = 0.08f)
                )
            }

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 52.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = "from",
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.5.sp
            ),
            color = Color(0xFF9CA3AF)
        )

        Spacer(modifier = Modifier.height(3.dp))

        Image(
            painter = painterResource(id = R.drawable.lg),
            contentDescription = "Company Logo",
            modifier = Modifier.height(35.dp),
            colorFilter = ColorFilter.tint(Color(0xFF6B7280))
        )
    }
}
