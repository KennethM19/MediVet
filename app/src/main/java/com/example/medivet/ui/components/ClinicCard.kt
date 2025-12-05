package com.example.medivet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medivet.R
import com.example.medivet.model.model.ClinicResponse

@Composable
fun ClinicCard(clinic: ClinicResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Nombre y RUC
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo_titulo), // Placeholder
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = clinic.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "RUC: ${clinic.ruc}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Dirección
            Row {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF00BFA5), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${clinic.address}, ${clinic.district}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            // Teléfono
            if (clinic.phone.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF00BFA5), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = clinic.phone, style = MaterialTheme.typography.bodyMedium)
                }
            }
            // Servicio (si tiene)
            clinic.service?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Servicio: ${it.name}",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF00796B)
                )
            }
        }
    }
}