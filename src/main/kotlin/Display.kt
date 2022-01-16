import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color

const val DISPLAY_WIDTH = 64
const val DISPLAY_HEIGHT = 32

class Display(
    private val canvas: Canvas,
    scale: Int = 5
) {
    private val frameBuffer: Array<Array<Double>> = Array(canvas.width.toInt()) {
        Array(canvas.height.toInt()) { 0.0 }
    }

    init {
        for (i in 0 until DISPLAY_WIDTH * scale) {
            for (j in 0 until DISPLAY_HEIGHT * scale) {
                frameBuffer[i][j] = 1.0
            }
        }
    }

    fun drawBuffer() {
        for (x in 0 until canvas.width.toInt()) {
            for (y in 0 until canvas.height.toInt()) {
                val color = if (getPixel(x, y) > 0.0) Color.GREEN else Color.BLACK
                canvas.graphicsContext2D.pixelWriter.setColor(x, y, color)
            }
        }
    }

    private fun getPixel(x: Int, y: Int): Double {
        return frameBuffer[x][y]
    }

    fun reset() {
        frameBuffer.forEachIndexed { index, line -> line[index] = 0.0 }
    }

}