package com.example.medivet.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.medivet.R
import com.example.medivet.view.navigation.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(navController: NavHostController) {
    val codeLength = 6
    var code by remember { mutableStateOf(List(codeLength) { "" }) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            AuthenticationLogo()
            Spacer(modifier = Modifier.height(60.dp))
            AuthInstructionText()
            Spacer(modifier = Modifier.height(16.dp))
            AuthCodeInputFields(code) { index, value ->
                code = code.toMutableList().also { it[index] = value }
            }
            Spacer(modifier = Modifier.height(24.dp))
            SendAuthenticationButton(navController, code.joinToString(""))
        }
    }
}

@Composable
fun AuthenticationLogo() {
    Image(
        painter = painterResource(id = R.drawable.logo_titulo),
        contentDescription = "Logo",
        modifier = Modifier
            .size(250.dp)
            .offset(y = 30.dp)
    )
}

// Texto de instrucción
@Composable
fun AuthInstructionText() {
    Text(
        "Se envió un código a example@example.com",
        color = Color.Black,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

// Input de código en recuadros individuales
@Composable
fun AuthCodeInputFields(code: List<String>, onCodeChange: (Int, String) -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box {
        // Campo invisible para capturar todo el input
        BasicTextField(
            value = code.joinToString(""),
            onValueChange = { input ->
                val digits = input.filter { it.isDigit() }.take(6)
                digits.forEachIndexed { i, c -> onCodeChange(i, c.toString()) }
                for (i in digits.length until 6) onCodeChange(i, "")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = Color.Transparent),
            cursorBrush = SolidColor(Color.Transparent), // <- Oculta el cursor
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .focusRequester(focusRequester)
        )


        // Los 6 recuadros visibles
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            code.forEach { digit ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .background(
                            Color.White.copy(alpha = 0.85f),
                            shape = MaterialTheme.shapes.small
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = digit,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
}

@Composable
fun SendAuthenticationButton(navController: NavHostController, authcode: String) {
    Button(
        onClick = {
            navController.navigate(AppScreens.MainScreen.route)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text("Registrarte")
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAuthenticationFirstScreen() {
    val navController = rememberNavController()

    AuthenticationScreen(navController = navController)
}
