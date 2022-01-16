import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.layout.StackPane
import javafx.stage.Stage


class FxApp: Application() {
    override fun start(stage: Stage) {
        val canvas = Canvas(600.0, 400.0)
        val chip8 = Chip8(canvas)
        stage.title = "8-Chip"
        stage.scene = Scene(StackPane(canvas), 800.0, 800.0)
        chip8.display.drawBuffer()
        stage.show()
    }
}

fun main() {
    Application.launch(FxApp::class.java)
}