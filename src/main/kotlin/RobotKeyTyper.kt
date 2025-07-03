import java.awt.Robot
import java.awt.event.KeyEvent

class RobotKeyTyper : KeyTyper {
    private val robot: Robot

    init {
        this.robot = Robot()
        robot.setAutoDelay(10)
    }

    override fun typeChar(c: Char) {
        try {
            val upper = Character.isUpperCase(c)
            var keyCode = KeyEvent.getExtendedKeyCodeForChar(c.code)
            if (keyCode == KeyEvent.VK_UNDEFINED && asciiMap.containsKey(c)) {
                keyCode = asciiMap.get(c)!!
            }
            if (keyCode == KeyEvent.VK_UNDEFINED) return

            val needsShift = needsShift(c)

            if (needsShift) robot.keyPress(KeyEvent.VK_SHIFT)
            robot.keyPress(keyCode)
            robot.keyRelease(keyCode)
            if (needsShift) robot.keyRelease(KeyEvent.VK_SHIFT)
        } catch (e: IllegalArgumentException) {
            // ignore
        }
    }

    private fun needsShift(c: Char): Boolean {
        return ")(*&^%$#@!~:{}|<>?+_".indexOf(c) >= 0 || Character.isUpperCase(c)
    }

    companion object {
        private val asciiMap: MutableMap<Char?, Int?> = HashMap<Char?, Int?>()

        init {
            run {
                var c = 'A'
                while (c <= 'Z') {
                    Companion.asciiMap.put(c, KeyEvent.getExtendedKeyCodeForChar(c.code))
                    c++
                }
            }
            run {
                var c = 'a'
                while (c <= 'z') {
                    Companion.asciiMap.put(c, KeyEvent.getExtendedKeyCodeForChar(c.code))
                    c++
                }
            }
            var c = '0'
            while (c <= '9') {
                asciiMap.put(c, KeyEvent.getExtendedKeyCodeForChar(c.code))
                c++
            }
            asciiMap.put(' ', KeyEvent.VK_SPACE)
            asciiMap.put('\n', KeyEvent.VK_ENTER)
            asciiMap.put('.', KeyEvent.VK_PERIOD)
            asciiMap.put(',', KeyEvent.VK_COMMA)
            asciiMap.put(';', KeyEvent.VK_SEMICOLON)
            asciiMap.put(':', KeyEvent.VK_COLON)
            asciiMap.put('!', KeyEvent.VK_EXCLAMATION_MARK)
            asciiMap.put('?', KeyEvent.VK_SLASH)
            asciiMap.put('-', KeyEvent.VK_MINUS)
            asciiMap.put('_', KeyEvent.VK_UNDERSCORE)
            asciiMap.put('=', KeyEvent.VK_EQUALS)
            asciiMap.put('+', KeyEvent.VK_PLUS)
            asciiMap.put('(', KeyEvent.VK_LEFT_PARENTHESIS)
            asciiMap.put(')', KeyEvent.VK_RIGHT_PARENTHESIS)
            asciiMap.put('[', KeyEvent.VK_OPEN_BRACKET)
            asciiMap.put(']', KeyEvent.VK_CLOSE_BRACKET)
            asciiMap.put('{', KeyEvent.VK_BRACELEFT)
            asciiMap.put('}', KeyEvent.VK_BRACERIGHT)
            asciiMap.put('<', KeyEvent.VK_LESS)
            asciiMap.put('>', KeyEvent.VK_GREATER)
            asciiMap.put('/', KeyEvent.VK_SLASH)
            asciiMap.put('\\', KeyEvent.VK_BACK_SLASH)
            asciiMap.put('"', KeyEvent.VK_QUOTE)
            asciiMap.put('\'', KeyEvent.VK_QUOTE)
            asciiMap.put('@', KeyEvent.VK_AT)
            asciiMap.put('#', KeyEvent.VK_NUMBER_SIGN)
            asciiMap.put('$', KeyEvent.VK_DOLLAR)
            asciiMap.put('%', KeyEvent.VK_5) // handled specially
            asciiMap.put('^', KeyEvent.VK_CIRCUMFLEX)
            asciiMap.put('&', KeyEvent.VK_AMPERSAND)
            asciiMap.put('*', KeyEvent.VK_ASTERISK)
            asciiMap.put('`', KeyEvent.VK_BACK_QUOTE)
            asciiMap.put('~', KeyEvent.VK_DEAD_TILDE)
        }
    }
}
