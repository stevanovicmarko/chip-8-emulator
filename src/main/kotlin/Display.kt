const val DISPLAY_WIDTH = 64
const val DISPLAY_HEIGHT = 32

class Display(
    width: Int,
    height: Int,
    scale: Int = 5
)
{
    private val frameBuffer: Array<Array<Double>> = Array(width) {
        Array(height) { 0.0 }
    }

    init {
        for (i in 0 until DISPLAY_WIDTH * scale) {
            for (j in 0 until DISPLAY_HEIGHT * scale) {
                frameBuffer[i][j] = 1.0
            }
        }
    }

    fun getPixel(x: Int, y: Int): Double {
        return frameBuffer[x][y]
    }

    fun reset() {
        frameBuffer.forEachIndexed { index, line -> line[index] = 0.0 }
    }

}