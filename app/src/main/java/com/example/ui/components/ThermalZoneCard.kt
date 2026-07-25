package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ThermalZone
import com.example.ui.theme.*

@Composable
fun ThermalZoneCard(
    zone: ThermalZone,
    modifier: Modifier = Modifier
) {
    val tempColor = when {
        zone.tempC >= 68f -> AlertRed
        zone.tempC >= 55f -> AmberWarning
        zone.tempC >= 40f -> MatrixGreen
        else -> CyberCyan
    }

    val statusLabel = when {
        zone.tempC >= 68f -> "CRITICAL"
        zone.tempC >= 55f -> "WARM"
        zone.tempC >= 40f -> "NORMAL"
        else -> "COOL"
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceCard)
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .testTag("thermal_zone_card_${zone.id}"),
        color = DarkSurfaceCard
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = zone.formattedName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = zone.sysfsPath,
                        fontSize = 10.sp,
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${zone.tempC}°C",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = tempColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(tempColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            color = tempColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Temperature meter bar (0°C to 90°C scale)
            val normalizedTemp = (zone.tempC / 90f).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { normalizedTemp },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(2.5.dp)),
                color = tempColor,
                trackColor = DarkSurfaceVariant
            )
        }
    }
}
