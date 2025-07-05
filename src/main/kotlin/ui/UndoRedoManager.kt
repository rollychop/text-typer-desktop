package ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import util.FixedStack

class UndoRedoManager(initial: TextFieldValue) {

    private val undoStack = FixedStack<TextFieldValue>(maxSize = 100)
    private val redoStack = FixedStack<TextFieldValue>(maxSize = 100)

    private var current: TextFieldValue = initial

    private val _hasUndo = mutableStateOf(false)
    private val _hasRedo = mutableStateOf(false)

    val hasUndo: State<Boolean> get() = _hasUndo
    val hasRedo: State<Boolean> get() = _hasRedo

    fun getCurrent(): TextFieldValue = current

    fun push(newValue: TextFieldValue) {
        if (newValue.text != current.text) {
            undoStack.push(current)
            current = newValue
            redoStack.clear()
            updateStates()
        }
    }

    fun undo(): TextFieldValue? {
        val previous = undoStack.pop()
        if (previous != null) {
            redoStack.push(current)
            current = previous
            updateStates()
        }
        return previous
    }

    fun redo(): TextFieldValue? {
        val next = redoStack.pop()
        if (next != null) {
            undoStack.push(current)
            current = next
            updateStates()
        }
        return next
    }

    private fun updateStates() {
        _hasUndo.value = undoStack.isNotEmpty
        _hasRedo.value = redoStack.isNotEmpty
    }
}
