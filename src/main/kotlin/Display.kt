import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color

const val DISPLAY_WIDTH = 64
const val DISPLAY_HEIGHT = 32
const val SPRITE_HEIGHT = 5u

class Display(
    private val memory: Memory,
    private val canvas: Canvas
) {
    private val scale = (canvas.width / DISPLAY_WIDTH).toInt()
    private val frameBuffer: Array<Array<Double>> = Array(DISPLAY_HEIGHT) {
        Array(DISPLAY_WIDTH) { 0.0 }
    }

    init {
        canvas.isFocusTraversable = true
        // clear with BEIGE for debug purposes
        for (h in 0 until DISPLAY_HEIGHT) {
            for (w in 0 until DISPLAY_WIDTH) {
                canvas.graphicsContext2D.fill = Color.BEIGE
                canvas.graphicsContext2D.fillRect(w.toDouble() * scale, h.toDouble() * scale, 10.0, 10.0)
            }
        }
        clearBuffer()
    }

    fun clearBuffer() {
        for (column in frameBuffer) {
            column.fill(0.0)
        }
    }

    private fun drawBuffer() {
        for(h in 0 until DISPLAY_HEIGHT) {
            for (w in 0 until  DISPLAY_WIDTH) {
                drawPixel(h * scale, w * scale, frameBuffer[h][w])
            }
        }
    }

    private fun drawPixel(x: Int, y: Int, value: Double) {
        canvas.graphicsContext2D.fill = if (value > 0.0) Color.GREEN else Color.BLACK
        val pixelSize = scale.toDouble()
        canvas.graphicsContext2D.fillRect(y.toDouble(), x.toDouble(), pixelSize, pixelSize)
    }

    fun drawSprite(height: Int, width: Int, spriteAddress: Int, size: Int): UByte {
        var pixelCollision: UByte = 0u

        for (lh in 0 until size) {
            val line = memory.memory[spriteAddress + lh]
            for (lw in 0 until CHAR_SET_WIDTH) {
                val bitToCheck = (0b10000000 shr lw).toUByte()
                if ((line and bitToCheck) == 0u.toUByte()) {
                    continue
                }
                val ph = (height + lh) % DISPLAY_HEIGHT
                val pw = (width + lw) % DISPLAY_WIDTH
                if (frameBuffer[ph][pw] >= 1.0) {
                    pixelCollision = 1u
                }

                frameBuffer[ph][pw] = ((frameBuffer[ph][pw]).toInt() xor 1).toDouble()
            }
        }
        drawBuffer()
        return pixelCollision
    }

}