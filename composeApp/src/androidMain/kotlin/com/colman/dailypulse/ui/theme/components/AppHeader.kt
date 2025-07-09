package com.colman.dailypulse.ui.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.colman.dailypulse.R


@Composable
fun AppHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "DailyPulse",
            fontSize = 46.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7B61FF)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_heartbeat),
            contentDescription = "Heartbeat Icon",
            modifier = Modifier.size(46.dp),
        )

    }

    Text(
        text = "Steady growth. Unstoppable momentum.",
        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        color = Color.DarkGray,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}
