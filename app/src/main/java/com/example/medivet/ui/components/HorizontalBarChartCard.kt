package com.example.medivet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.medivet.model.model.VaccineChartData

/**
 * Gráfico de barras horizontales para ranking de vacunas.
 * Diseño Material 3 con degradado de colores.
 */
@Composable
fun HorizontalBarChartCard(
    title: String,
    subtitle: String,
    data: List<VaccineChartData>,
    color: Color,
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
            // Encabezado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = color
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Badge con total
                if (data.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = color.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "Top ${data.size}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = color
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (data.isEmpty()) {
                // Estado vacío
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📊",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No hay datos de vacunas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                // Calcular valor máximo para escalar las barras
                val maxCount = remember(data) { data.maxOfOrNull { it.count } ?: 1f }

                // Lista de vacunas con barras
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    data.forEachIndexed { index, vaccine ->
                        VaccineBarItem(
                            ranking = index + 1,
                            vaccineName = vaccine.vaccineName,
                            count = vaccine.count.toInt(),
                            maxCount = maxCount,
                            color = color
                        )
                    }
                }
            }
        }
    }
}

/**
 * Item individual de barra horizontal con ranking.
 */
@Composable
private fun VaccineBarItem(
    ranking: Int,
    vaccineName: String,
    count: Int,
    maxCount: Float,
    color: Color
) {
    val percentage = (count / maxCount)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Medalla de ranking
        Surface(
            modifier = Modifier.size(32.dp),
            shape = RoundedCornerShape(8.dp),
            color = when (ranking) {
                1 -> Color(0xFFFFD700) // Oro
                2 -> Color(0xFFC0C0C0) // Plata
                3 -> Color(0xFFCD7F32) // Bronce
                else -> color.copy(alpha = 0.2f)
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ranking.toString(),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (ranking <= 3) Color.White else color
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Contenedor de nombre y barra
        Column(modifier = Modifier.weight(1f)) {
            // Nombre de la vacuna y cantidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = vaccineName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Barra de progreso horizontal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        color = color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(percentage)
                        .fillMaxHeight()
                        .background(
                            color = color,
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
    }
}