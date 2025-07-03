import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun TextTyperUI(
    isTyping: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    inputText: String,
    onTextChange: (String) -> Unit,
    startDelay: Long,
    onChangeStartDelay: (Long) -> Unit,
    stillRunning: Boolean
) {
    var countdown by remember { mutableStateOf(0L) }

    // Handle countdown timer
    LaunchedEffect(isTyping, startDelay) {
        if (isTyping) {
            countdown = startDelay
            while (countdown > 0) {
                delay(100)
                countdown -= 100
            }
        } else {
            countdown = 0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Text Typer",
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedTextField(
                    label = { Text("Start Delay (ms)") },
                    value = startDelay.toString(),
                    onValueChange = {
                        onChangeStartDelay(it.toLongOrNull() ?: 300L)
                    },
                    modifier = Modifier.width(180.dp),
                    enabled = !isTyping
                )
                if (!isTyping) {
                    Button(onClick = onStart) {
                        Text("Start")
                    }
                } else {
                    Button(
                        onClick = onStop,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text("Stop")
                    }
                }

            }

            if (isTyping && countdown > 0L) {
                val percent = ((startDelay - countdown).toFloat() / startDelay * 100).toInt()
                Text(
                    text = "Starting in ${countdown / 1000.0f}s ($percent%)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(stillRunning || isTyping) {
                var fakeBuffer by remember { mutableStateOf("") }
                val fs = remember { FocusRequester() }
                OutlinedTextField(
                    value = fakeBuffer,
                    onValueChange = { fakeBuffer = it },
                    label = { Text("Typing output...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(fs)
                        .heightIn(min = 120.dp), // ~5 lines visually
                    maxLines = 5,
                    isError = stillRunning && !isTyping,
                )
                LaunchedEffect(Unit) {
                    delay(30)
                    fs.requestFocus()
                }
            }
        }

        BasicTextField(
            enabled = !isTyping,
            value = inputText,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 8.dp)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
                .weight(1f),
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                        .padding(12.dp)
                ) {
                    if (inputText.isEmpty()) {
                        Text(
                            "Enter text to type...",
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
