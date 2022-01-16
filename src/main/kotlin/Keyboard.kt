import javafx.scene.Node

data class Keyboard(val source: Node) {
    private val hostKeys = listOf(
        "1", "2", "3",
        "q", "w", "e",
        "a", "s", "d",
        "x", "z", "c",
        "4", "r", "f", "v"
    )
    private val emulatedKeys = Array(hostKeys.size) { false }

    init {
        source.setOnKeyPressed {
            val keyIndex = hostKeys.indexOf(it.text)
            if (keyIndex > -1) {
                emulatedKeys[keyIndex] = true
            }
        }

        source.setOnKeyReleased {
            val keyIndex = hostKeys.indexOf(it.text)
            if (keyIndex > -1) {
                emulatedKeys[keyIndex] = false
            }
        }
    }

    fun hasKeyDown(): Boolean {
        return emulatedKeys.any { it }
    }

    fun isKeyDown(index: Int): Boolean {
        return emulatedKeys[index]
    }
}