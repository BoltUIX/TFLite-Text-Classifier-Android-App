package com.example.tfliteapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tfliteapp.ui.theme.TFliteAppTheme

// Main activity class to set up the app
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge display for immersive UI
        TextClassifier.initialize(this) // Initialize the text classifier model
        setContent {
            TFliteAppTheme { // Apply the app's theme
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(modifier = Modifier.padding(innerPadding)) // Render the home screen with padding
                }
            }
        }
    }
}

// Preview composable for HomeScreen in Android Studio
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}

// Composable function for the home screen UI
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") } // State for user input text
    var res by remember { mutableStateOf("") } // State for prediction result
    var isLoading by remember { mutableStateOf(false) } // State for loading indicator during prediction
    var error by remember { mutableStateOf("") } // State for error messages

    Column(
        modifier = modifier
            .fillMaxSize() // Fill the entire screen
            .padding(16.dp) // Add padding around the content
            .verticalScroll(rememberScrollState()), // Make the column scrollable if content overflows
        horizontalAlignment = Alignment.CenterHorizontally, // Center content horizontally
        verticalArrangement = Arrangement.Center // Center content vertically
    ) {
        // Card to group the input field for better visual appeal
        Card(
            modifier = Modifier
                .fillMaxWidth() // Fill width of parent
                .padding(8.dp) // Padding around the card
        ) {
            TextField(
                value = text,
                onValueChange = { text = it }, // Update state on text change
                placeholder = { Text("Enter your post here...") }, // Placeholder text
                modifier = Modifier
                    .fillMaxWidth() // Fill width
                    .height(150.dp) // Fixed height for input area
                    .padding(8.dp), // Padding inside the card
                maxLines = 10, // Allow up to 10 lines
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start) // Left-align text
            )
        }

        Spacer(modifier = Modifier.height(16.dp)) // Space between elements

        // Button to trigger prediction
        Button(
            onClick = {
                if (text.isBlank()) { // Check for empty input
                    error = "Please enter some text."
                    return@Button
                }
                error = "" // Clear error
                isLoading = true // Show loading
                val result = TextClassifier.predict(text) // Call prediction (assume it returns String)
                res = result // Update result state
                isLoading = false // Hide loading
                Log.d("Prediction", result) // Log the result for debugging
            },
            modifier = Modifier
                .fillMaxWidth(0.8f) // 80% width for button
                .height(50.dp) // Fixed button height
        ) {
            Text("Predict", style = MaterialTheme.typography.bodyLarge) // Button text with theme style
        }

        Spacer(modifier = Modifier.height(16.dp)) // Space

        // Display loading indicator if predicting
        if (isLoading) {
            CircularProgressIndicator() // Show progress circle
        }

        // Display error if any
        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error, // Use error color from theme
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp)) // Space

        // Display prediction result with bold label
        Text(
            text = "Prediction Result:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), // Bold title
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = res,
            style = MaterialTheme.typography.bodyLarge, // Larger text for result
            modifier = Modifier.fillMaxWidth()
        )
    }
}