package util

class FixedStack<T>(private val maxSize: Int = 100) {
    private val deque = ArrayDeque<T>(maxSize)

    fun push(item: T) {
        if (deque.size >= maxSize) {
            deque.removeFirst() // 🔁 Remove oldest
        }
        deque.addLast(item)
    }

    fun pop(): T? = if (deque.isNotEmpty()) deque.removeLast() else null

    fun peek(): T? = deque.lastOrNull()

    fun clear() = deque.clear()

    val isNotEmpty: Boolean get() = deque.isNotEmpty()
    val isEmpty: Boolean get() = deque.isEmpty()
    val size: Int get() = deque.size
}
