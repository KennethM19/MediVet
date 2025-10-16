package com.example.medivet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.medivet.model.PetResponse

@Composable
fun PetCard(
    pet: PetResponse, // El parámetro correcto
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            // --- CORRECCIONES AQUÍ ---
            Image(
                painter = rememberAsyncImagePainter(pet.photo), // Usa 'pet'
                contentDescription = pet.name, // Usa 'pet'
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = pet.name, // Usa 'pet'
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar mascota")
                    }
                }
                // --- Y AQUÍ ---
                Text("Sexo: ${pet.sex_id}", style = MaterialTheme.typography.bodySmall) // Usa 'pet'
                Text("Peso: ${pet.weight}", style = MaterialTheme.typography.bodySmall) // Usa 'pet'
                //Text("Edad: ${pet.}", style = MaterialTheme.typography.bodySmall) // Usa 'pet'
                Text("Raza: ${pet.breed_id}", style = MaterialTheme.typography.bodySmall) // Usa 'pet'
            }
        }
    }
}
