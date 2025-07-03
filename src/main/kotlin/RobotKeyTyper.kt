import java.awt.Robot
import java.awt.event.KeyEvent

class RobotKeyTyper : KeyTyper {
    private val robot = Robot()

    init {
        robot.autoDelay = 10
    }

    override fun typeChar(c: Char) {
        try {
            val shift = needsShift(c)
            val normalizedChar = if (c.isLetter()) c.uppercaseChar() else c
            val keyCode = keyMap[normalizedChar] ?: KeyEvent.getExtendedKeyCodeForChar(normalizedChar.code)
            if (keyCode == KeyEvent.VK_UNDEFINED) return

            if (shift) robot.keyPress(KeyEvent.VK_SHIFT)
            robot.keyPress(keyCode)
            robot.keyRelease(keyCode)
            if (shift) robot.keyRelease(KeyEvent.VK_SHIFT)
            TypeSoundPlayer.playClick()
        } catch (_: Exception) {
        }
    }

    private fun needsShift(c: Char): Boolean {
        return c.isUpperCase() || shiftChars.contains(c)
    }

    companion object {
        private val shiftChars = setOf(
            '~', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')',
            '_', '+', '{', '}', '|', ':', '"', '<', '>', '?'
        )

        val keyMap = mapOf(
            // Symbols requiring shift
            '~' to KeyEvent.VK_BACK_QUOTE,
            '!' to KeyEvent.VK_1,
            '@' to KeyEvent.VK_2,
            '#' to KeyEvent.VK_3,
            '$' to KeyEvent.VK_4,
            '%' to KeyEvent.VK_5,
            '^' to KeyEvent.VK_6,
            '&' to KeyEvent.VK_7,
            '*' to KeyEvent.VK_8,
            '(' to KeyEvent.VK_9,
            ')' to KeyEvent.VK_0,
            '_' to KeyEvent.VK_MINUS,
            '+' to KeyEvent.VK_EQUALS,
            '{' to KeyEvent.VK_OPEN_BRACKET,
            '}' to KeyEvent.VK_CLOSE_BRACKET,
            '|' to KeyEvent.VK_BACK_SLASH,
            ':' to KeyEvent.VK_SEMICOLON,
            '"' to KeyEvent.VK_QUOTE,
            '<' to KeyEvent.VK_COMMA,
            '>' to KeyEvent.VK_PERIOD,
            '?' to KeyEvent.VK_SLASH,

            // Non-shift symbols
            '`' to KeyEvent.VK_BACK_QUOTE,
            '-' to KeyEvent.VK_MINUS,
            '=' to KeyEvent.VK_EQUALS,
            '[' to KeyEvent.VK_OPEN_BRACKET,
            ']' to KeyEvent.VK_CLOSE_BRACKET,
            '\\' to KeyEvent.VK_BACK_SLASH,
            ';' to KeyEvent.VK_SEMICOLON,
            '\'' to KeyEvent.VK_QUOTE,
            ',' to KeyEvent.VK_COMMA,
            '.' to KeyEvent.VK_PERIOD,
            '/' to KeyEvent.VK_SLASH,

            ' ' to KeyEvent.VK_SPACE,
            '\n' to KeyEvent.VK_ENTER
        )
    }
}
