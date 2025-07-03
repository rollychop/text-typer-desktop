import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun App(
    isTyping: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var timeMillis by remember { mutableLongStateOf(3000) }

    val typer = remember<KeyTyper> { RobotKeyTyper() }
    val shouldType = rememberUpdatedState(isTyping)
    val stopCallback = rememberUpdatedState(onStop)
    val scope = rememberCoroutineScope()
    var stillRunning by remember { mutableStateOf(false) }

    // Track job state to cancel if needed
    var job by remember { mutableStateOf<Job?>(null) }

    DisposableEffect(isTyping) {
        if (isTyping) {
            val activeFlag = AtomicBoolean(true)
            job = scope.launch(Dispatchers.Default) {
                delay(timeMillis)
                for (char in inputText) {
                    stillRunning = true
                    if (!activeFlag.get()) {
                        stillRunning = false
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
        stillRunning=stillRunning,
        startDelay = timeMillis,
        onChangeStartDelay = { timeMillis = it },
        isTyping = isTyping,
        onStart = { if (inputText.isNotBlank()) onStart() },
        onStop = onStop,
        inputText = inputText,
        onTextChange = { inputText = it }
    )
}
