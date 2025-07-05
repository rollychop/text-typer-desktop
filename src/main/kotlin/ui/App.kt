package ui

import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import util.KeyTyper
import util.RobotKeyTyper
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun App(
    isTyping: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    var inputText by remember { mutableStateOf(TextFieldValue()) }
    var timeMillis by remember { mutableStateOf("3000") }

    val typer = remember<KeyTyper> { RobotKeyTyper() }
    rememberUpdatedState(isTyping)
    val stopCallback = rememberUpdatedState(onStop)
    val scope = rememberCoroutineScope()
    val stillRunning by remember { mutableStateOf(false) }

    // Track job state to cancel if needed
    var job by remember { mutableStateOf<Job?>(null) }

    DisposableEffect(isTyping) {
        if (isTyping) {
            val activeFlag = AtomicBoolean(true)
            job = scope.launch(Dispatchers.Default) {
                delay(timeMillis.toLongOrNull() ?: 3000L)
                for (char in inputText.text) {
                    if (!activeFlag.get()) {
                        break
                    }
                    typer.typeChar(char)
                }
                stopCallback.value()
            }

            onDispose {
                activeFlag.set(false)
                job?.cancel()
            }
        } else {
            onDispose { job?.cancel() }
        }
    }

    TextTyperUI(
        stillRunning = stillRunning,
        startDelay = timeMillis,
        onChangeStartDelay = { timeMillis = it },
        isTyping = isTyping,
        onStart = {
            if (inputText.text.isNotBlank() && timeMillis.toLongOrNull() != null)
                onStart()
        },
        onStop = onStop,
        inputText = inputText,
        onTextChange = { inputText = it }
    )
}
