import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application


fun main(): Unit = application {
    var isTyping by mutableStateOf(false)

    Window(
        onCloseRequest = ::exitApplication,
        title = "Text Typer",
        icon = painterResource("text_field.png"),
        onKeyEvent = onKeyEvent@{
            if (it.key == Key.Escape && isTyping) {
                isTyping = false
                return@onKeyEvent true
            }
            false
        }
    ) {
        App(
            isTyping = isTyping,
            onStart = { isTyping = true },
            onStop = { isTyping = false }
        )
    }
}

