import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color

const val DISPLAY_WIDTH = 64
const val DISPLAY_HEIGHT = 32


class Display(
    private val memory: Memory,
    private val canvas: Canvas
) {
    private val scale = (canvas.width / DISPLAY_WIDTH).toInt()
    private val frameBuffer: Array<Array<Double>> = Array(canvas.width.toInt()) {
        Array(canvas.height.toInt()) { 0.0 }
    }

    init {
        canvas.isFocusTraversable = true
        for (i in 0 until DISPLAY_WIDTH * scale) {
            for (j in 0 until DISPLAY_HEIGHT * scale) {
                frameBuffer[i][j] = 0.0
            }
        }
    }

    fun clearBuffer() {
        for (x in 0 until canvas.width.toInt()) {
            for (y in 0 until canvas.height.toInt()) {
                val color = if (getPixel(x, y) > 0.0) Color.BLACK else Color.BISQUE
                canvas.graphicsContext2D.pixelWriter.setColor(x, y, color)
            }
        }
    }

    private fun getPixel(x: Int, y: Int): Double {
        return frameBuffer[x][y]
    }

    private fun drawPixel(x: Int, y: Int, color: Color) {
        canvas.graphicsContext2D.fill = color
        val pixelSize = scale.toDouble()
        canvas.graphicsContext2D.fillRect(x.toDouble(), y.toDouble(), pixelSize, pixelSize)
    }

//    fun reset() {
//        frameBuffer.forEachIndexed { index, line -> line[index] = 0.0 }
//    }

    fun drawSprite(height: Int, width: Int, spriteAddress: Int, size: Int) {
        for (lh in 0 until size) {
            val line = memory.memory[spriteAddress + lh]
            for (lw in 0 until CHAR_SET_WIDTH) {
                val bitToCheck = (0b10000000 shr lw).toUByte()
                val value = line and bitToCheck
                val color = if (value != 0u.toUByte()) {
                    Color.GREEN
                } else {
                    Color.BLACK
                }
                drawPixel((width + lw) * scale, (height + lh) * scale, color)
            }
        }
    }

}