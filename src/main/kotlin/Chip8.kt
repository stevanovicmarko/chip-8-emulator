import javafx.scene.canvas.Canvas

class Chip8(canvas: Canvas) {
    val display = Display(canvas)
    val keyboard = Keyboard(canvas)
    val memory = Memory()
    val registers = Registers()
    init {
        canvas.isFocusTraversable = true
    }
}