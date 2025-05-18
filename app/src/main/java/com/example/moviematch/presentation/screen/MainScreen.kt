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

/**
 * Main screen of the application, displayed on app launch.
 *
 * Provides options for:
 * - Language selection (SK/EN)
 * - Theme toggle (light/dark)
 * - Navigation to Login or Register screens
 *
 * @param navController Used to navigate to other screens in the app.
 * @param isDarkTheme Boolean indicating whether the current theme is dark.
 * @param onLanguageChange Callback to be invoked when a new language is selected.
 * @param onThemeToggle Callback to be invoked when theme toggle is triggered.
 */
@Composable
fun MainScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onLanguageChange: (String) -> Unit,
    onThemeToggle: () -> Unit,
) {
    // Select appropriate icon based on current theme
    //https://emojipedia.org/animated-noto-color-emoji/15.0/light-bulb
    val themeIcon = if (!isDarkTheme) R.drawable.light_bulb_on else R.drawable.light_bulb_off

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        /**
         * Header row allowing user to:
         * - Switch language to Slovak or English
         * - Toggle the theme via a clickable image in the center
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left box for Slovak language selection
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.language_sk),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.clickable { onLanguageChange("sk") },
                    fontSize = 16.sp
                )
            }

            // Center box with theme toggle icon
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = themeIcon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onThemeToggle() }
                )
            }

            // Right box for English language selection
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.language_en),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.clickable { onLanguageChange("en") },
                    fontSize = 16.sp
                )
            }
        }

        // Spacer pushing content vertically toward center
        Spacer(modifier = Modifier.weight(1f))

        // App title displayed prominently in the center
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(48.dp))

        /**
         * Button to navigate to the login screen
         */
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

        /**
         * Button to navigate to the registration screen
         */
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

        // Final spacer to maintain consistent bottom padding
        Spacer(modifier = Modifier.height(16.dp).weight(1f))
    }
}
