import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.layout.StackPane
import javafx.stage.Stage


class Chip8App: Application() {
    override fun start(stage: Stage) {
        val canvas = Canvas(600.0, 400.0)
        val chip8 = Chip8(canvas)
        stage.title = "8-Chip"
        stage.scene = Scene(StackPane(canvas), 800.0, 800.0)
        chip8.display.drawBuffer()
        chip8.display.drawSprite(10, 1, 0, 5)
        chip8.display.drawSprite(10, 6, 5, 5)
        chip8.display.drawSprite(10, 11, 10, 5)
        chip8.display.drawSprite(10, 16, 15, 5)

        stage.show()
    }
}

fun main() {
    Application.launch(Chip8App::class.java)
}