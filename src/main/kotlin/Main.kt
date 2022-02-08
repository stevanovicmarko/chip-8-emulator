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
        val canvas = Canvas(600.0, 400.0)
        val chip8 = Chip8(canvas)

        chip8.display.clearBuffer()
        chip8.display.drawSprite(10, 1, 0, 5)
        chip8.display.drawSprite(10, 10, 5, 5)
        chip8.display.drawSprite(10, 19, 10, 5)
        chip8.display.drawSprite(10, 28, 15, 5)

        chip8.registers.soundTimer = 10
        chip8.soundCard.soundEnabled = true

        stage.title = "8-Chip"
        stage.scene = Scene(StackPane(canvas), 800.0, 800.0)
        stage.show()
        println(chip8.memory.getOpcode(0x200).toString(16))
        println(chip8.memory.getOpcode(0x202).toString(16))


        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                chip8.sleep(200)
                if (chip8.registers.delayTimer > 0) {
                    chip8.sleep()
                    chip8.registers.delayTimer--
                }
                if (chip8.registers.soundTimer > 0) {
                    chip8.sleep()
                    chip8.registers.soundTimer--
                }
                if (chip8.registers.soundTimer == 0.toByte()) {
                    chip8.soundCard.soundEnabled = false
                }
            }
        }

    }
}


fun main() {
    Application.launch(Chip8App::class.java)
}