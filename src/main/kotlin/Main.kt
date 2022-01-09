import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.stage.Stage

class FxApp: Application() {
    override fun start(stage: Stage) {
        val canvas = Canvas(600.0, 400.0)
        val chip8 = Chip8(canvas.width.toInt(), canvas.height.toInt())
        stage.title = "8-Chip"

        for (x in 0 until canvas.width.toInt()) {
            for (y in 0 until  canvas.height.toInt()) {
                val color = if (chip8.display.getPixel(x, y) > 0.0) Color.GREEN else Color.BLACK
                canvas.graphicsContext2D.pixelWriter.setColor(x, y, color)
            }
        }
        stage.scene = Scene(StackPane(canvas), 800.0, 800.0)
        stage.show()
    }
}

fun main() {
    Application.launch(FxApp::class.java)
}