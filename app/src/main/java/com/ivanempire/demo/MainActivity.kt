package com.ivanempire.demo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.ivanempire.demo.ui.theme.DemoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable fullscreen mode
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            DemoTheme {
                var inference by remember { mutableStateOf<LlmInference?>(null) }
                var isLoading by remember { mutableStateOf(true) }

                // Load the model asynchronously
                LaunchedEffect(Unit) {
                    inference = withContext(Dispatchers.IO) { initPipeline(this@MainActivity) }
                    isLoading = false
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator()
                        } else {
                            inference?.let {
                                LlmUI(inference = it)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun initPipeline(context: Context): LlmInference {
    val options = LlmInference.LlmInferenceOptions.builder()
        .setModelPath("/data/local/tmp/gemma/gemma2-2b-it-gpu-int8.bin")
        .setMaxTokens(1000)
        .setMaxTopK(40)
        .build()

    return LlmInference.createFromOptions(context, options)
}

@Composable
fun LlmUI(inference: LlmInference) {
    var result by remember { mutableStateOf("No response yet.") }
    var isGenerating by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isGenerating) {
            CircularProgressIndicator()
        } else {
            Text(text = result, modifier = Modifier.padding(16.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                isGenerating = true
                CoroutineScope(Dispatchers.IO).launch {
                    val response = inference.generateResponse("Tell me a short story about a house")
                    withContext(Dispatchers.Main) {
                        result = response
                        isGenerating = false
                    }
                }
            },
            enabled = !isGenerating
        ) {
            Text("Generate Story")
        }
    }
}