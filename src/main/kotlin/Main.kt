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

        chip8.registers.soundTimer = 10
//        chip8.soundCard.soundEnabled = true

        stage.title = "8-Chip"
        stage.scene = Scene(StackPane(canvas), 800.0, 800.0)
        stage.show()
        chip8.registers.pc = 0x010
        chip8.registers.i = 0x0au
        chip8.registers.v[0] = 0u
        chip8.registers.v[5] = 0u
        chip8.registers.v[8] = 0x03u
        chip8.execute(0xd505)

        chip8.registers.i = 0x00u
        chip8.registers.v[0] = 3u
        chip8.registers.v[5] = 3u
        chip8.registers.v[8] = 0x03u
        chip8.execute(0xd505)

        println("vf ${chip8.registers.v[0x0f].toString(16)}")


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