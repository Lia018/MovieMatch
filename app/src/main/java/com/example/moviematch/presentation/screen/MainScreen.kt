package com.example.moviematch.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moviematch.R




@Composable
fun MainScreen(

    navController: NavController,
    isDarkTheme: Boolean,
    onLanguageChange: (String) -> Unit,
    onThemeToggle: () -> Unit,
) {

    val themeIcon = if (!isDarkTheme) R.drawable.light_bulb_on else R.drawable.light_bulb_off

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.language_sk),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .clickable { onLanguageChange("sk") },
                    fontSize = 16.sp
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = themeIcon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onThemeToggle() },
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.language_en),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .clickable { onLanguageChange("en") },
                    fontSize = 16.sp
                )
            }
        }


        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(290.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = stringResource(id = R.string.login),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("register") },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(290.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = stringResource(id = R.string.register),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp).weight(1f))
    }
}
