package com.example.medivet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medivet.model.model.ChartData

/**
 * Gráfico circular para mostrar proporción de mascotas castradas.
 */
@Composable
fun PieChartCard(
    title: String,
    data: List<ChartData>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Título
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF1DB3A6)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (data.isEmpty()) {
                // Estado vacío
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay datos disponibles",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                // Representación visual simple de proporciones
                val total = data.sumOf { it.value.toDouble() }

                Column {
                    data.forEach { item ->
                        val percentage = (item.value / total * 100).toInt()

                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                                Text(
                                    text = "$percentage%",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF1DB3A6)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Barra de progreso
                            LinearProgressIndicator(
                                progress = percentage / 100f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp),
                                color = if (item.label.contains("Castrado", ignoreCase = true)) {
                                    Color(0xFF4CAF50)
                                } else {
                                    Color(0xFFFF9800)
                                },
                                trackColor = Color(0xFFE0E0E0)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${item.value.toInt()} mascotas",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}