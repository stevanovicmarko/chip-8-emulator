import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Chip8App : Application() {

    override fun start(stage: Stage) {
        val canvas = Canvas(640.0, 320.0)
        val chip8 = Chip8(canvas)

        chip8.display.clearBuffer()

        chip8.registers.soundTimer = 10u
//        chip8.soundCard.soundEnabled = true

        stage.title = "8-Chip"
        stage.scene = Scene(StackPane(canvas), 800.0, 800.0)
        stage.show()

        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                chip8.sleep(200)
                chip8.cycle()
                if (chip8.registers.delayTimer > 0u) {
                    chip8.sleep()
                    chip8.registers.delayTimer--
                }
                if (chip8.registers.soundTimer > 0u) {
                    chip8.sleep()
                    chip8.registers.soundTimer--
                }
                if (chip8.registers.soundTimer == 0.toUByte()) {
                    chip8.soundCard.soundEnabled = false
                }
            }
        }

    }
}


fun main() {
    Application.launch(Chip8App::class.java)
}