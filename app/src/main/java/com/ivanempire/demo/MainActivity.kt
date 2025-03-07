package com.ivanempire.demo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.ivanempire.demo.ui.theme.DemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inference = initPipeline(this)
        enableEdgeToEdge()
        setContent {
            DemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        inference = inference,
                        modifier = Modifier.padding(innerPadding)
                    )
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
fun Greeting(name: String, inference: LlmInference, modifier: Modifier = Modifier) {

    var result by remember { mutableStateOf("No response yet.") }

    Button(onClick = {
        result = inference.generateResponse("Tell me a short story about a house")
    }) {
        Text(
            text = "Tell me a short story about a house",
            modifier = modifier
        )
    }

    Text(text = result)
}