package ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import util.TextFieldUtils
import util.WhitespaceAndUnicodeVisualTransformation

const val IsLineNumberEnabled = false

@Composable
fun TextTyperUI(
    isTyping: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    inputText: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    startDelay: String,
    onChangeStartDelay: (String) -> Unit,
    stillRunning: Boolean
) {
    var countdown by remember { mutableLongStateOf(0L) }

    // Handle countdown timer
    LaunchedEffect(isTyping, startDelay) {
        if (isTyping) {
            countdown = startDelay.toLongOrNull() ?: 3000L
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
                    text = "Text Typer",
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedTextField(
                    label = { Text("Start Delay (ms)") },
                    value = startDelay,
                    onValueChange = {
                        onChangeStartDelay(it)
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
                Text(
                    text = "Starting in ${countdown / 1000.0f}s",
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
                        .heightIn(min = 120.dp),
                    maxLines = 5,
                    isError = stillRunning && !isTyping,
                )
                LaunchedEffect(Unit) {
                    delay(30)
                    fs.requestFocus()
                }
            }
        }

        val undoManager = remember { UndoRedoManager(inputText) }
        var debounceJob by remember { mutableStateOf<Job?>(null) }

        EditorToolbar(
            enabled = !isTyping,
            modifier = Modifier.fillMaxWidth(),
            actions = listOf(
                "Trim Lines",
                "Trim Empty",
                "Collapse Spaces",
                "Full Clean",
                "Undo",
                "Redo",
            ),
            onActionClick = { action, index ->
                val tv = when (action) {
                    "Trim Lines" -> TextFieldUtils.trimLines(inputText)
                    "Trim Empty" -> TextFieldUtils.trimEmptyLines(inputText)
                    "Collapse Spaces" -> TextFieldUtils.collapseSpaces(inputText)
                    "Full Clean" -> TextFieldUtils.fullClean(inputText, tabSize = 4)
                    "Undo" -> undoManager.undo()
                    "Redo" -> undoManager.redo()
                    else -> null
                }
                tv?.let {
                    onTextChange(it)
                    if (action != "Undo" && action != "Redo") {
                        undoManager.push(it)
                    }
                }

            }
        )

        val coroutineScope = rememberCoroutineScope()

        val lineNumbers = remember(inputText.text) {
            inputText.text
                .lines()
                .indices
                .joinToString("\n") { (it + 1).toString() }
        }
        Row(
            modifier = Modifier.fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))

        ) {
            val textStyle = TextStyle.Default
                .copy(fontFamily = notoFont)
            BasicTextField(
                enabled = !isTyping,
                value = inputText,
                onValueChange = {
                    onTextChange(it)

                    debounceJob?.cancel()
                    debounceJob = coroutineScope.launch {
                        delay(300L)
                        undoManager.push(it)
                    }
                },
                visualTransformation = WhitespaceAndUnicodeVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onPreviewKeyEvent { event ->
                        if (event.isCtrlPressed && event.key == Key.Z && event.type == KeyEventType.KeyDown) {
                            undoManager.undo()?.let { onTextChange(it) }
                            true
                        } else if (
                            (event.isCtrlPressed && event.isShiftPressed && event.key == Key.Z ||
                                    event.isCtrlPressed && event.key == Key.Y) &&
                            event.type == KeyEventType.KeyDown
                        ) {
                            undoManager.redo()?.let { onTextChange(it) }
                            true
                        } else {
                            false
                        }
                    },
                decorationBox = { innerTextField ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(all = 12.dp)
                    ) {
                        Row {
                            if (IsLineNumberEnabled) {
                                Text(
                                    text = lineNumbers,
                                    style = textStyle.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                    ),
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(start = 4.dp)
                                        .width(40.dp)
                                )
                            }
                            Box {
                                if (inputText.text.isEmpty()) {
                                    Text(
                                        "Enter text to type...",
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                innerTextField()
                            }
                        }
                    }
                },
                cursorBrush = remember { Brush.verticalGradient(listOf(Color.Red, Color.Green, Color.Blue)) },
                textStyle = textStyle
            )
        }
    }
}
