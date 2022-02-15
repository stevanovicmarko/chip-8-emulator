import javafx.scene.canvas.Canvas
import kotlinx.coroutines.delay
import java.io.File

val CHAR_SET: Array<UByte> = arrayOf(
    0xF0u,
    0x90u,
    0x90u,
    0x90u,
    0xF0u,
    0x20u,
    0x60u,
    0x20u,
    0x20u,
    0x70u,
    0xF0u,
    0x10u,
    0xF0u,
    0x80u,
    0xF0u,
    0xF0u,
    0x10u,
    0xF0u,
    0x10u,
    0xF0u,
    0x90u,
    0x90u,
    0xF0u,
    0x10u,
    0x10u,
    0xF0u,
    0x80u,
    0xF0u,
    0x10u,
    0xF0u,
    0xF0u,
    0x80u,
    0xF0u,
    0x90u,
    0xF0u,
    0xF0u,
    0x10u,
    0x20u,
    0x40u,
    0x40u,
    0xF0u,
    0x90u,
    0xF0u,
    0x90u,
    0xF0u,
    0xF0u,
    0x90u,
    0xF0u,
    0x10u,
    0xF0u,
    0xF0u,
    0x90u,
    0xF0u,
    0x90u,
    0x90u,
    0xE0u,
    0x90u,
    0xE0u,
    0x90u,
    0xE0u,
    0xF0u,
    0x80u,
    0x80u,
    0x80u,
    0xF0u,
    0xE0u,
    0x90u,
    0x90u,
    0x90u,
    0xE0u,
    0xF0u,
    0x80u,
    0xF0u,
    0x80u,
    0xF0u,
    0xF0u,
    0x80u,
    0xF0u,
    0x80u,
    0x80u
)

class Chip8(canvas: Canvas) {
    val memory = Memory()
    val display = Display(memory, canvas)
    val keyboard = Keyboard(canvas)
    val disassembler = Disassembler()
    val registers = Registers()
    val soundCard = SoundCard()

    init {
        val buffer = File((object {}.javaClass.getResource("test_opcode.ch8")?.toURI() ?: error("Invalid path")))
            .readBytes()
            .toTypedArray()
            .map { it.toUByte() }
            .toTypedArray()

        assert(buffer.size + LOAD_PROGRAM_ADDRESS <= MEMORY_SIZE) {"Not enough memory for ROM"}

        CHAR_SET.copyInto(memory.memory, CHAR_SET_ADDRESS)
        buffer.copyInto(memory.memory, LOAD_PROGRAM_ADDRESS.toInt())
        registers.pc = LOAD_PROGRAM_ADDRESS

        println(buffer.contentToString())
    }

    suspend fun sleep(sleepDuration: Long = TIMER_60_HZ.toLong()) {
        delay(sleepDuration)
    }

    fun execute(opcode: Int) {
        val (instruction, args) = disassembler.disassemble(opcode)
        println(instruction)
        println(args)
        println(instruction.id)

        when (instruction.id) {
            InstructionSet.CLS -> display.clearBuffer()
            InstructionSet.RET -> {
                registers.pc = registers.stackPop()
            }
            InstructionSet.JP_ADDR -> {
                registers.pc = args[0]
            }
            InstructionSet.CALL_ADDR -> {
                registers.stackPush(registers.pc)
                registers.pc = args[0]
            }
            InstructionSet.SE_VX_KK -> {
                if (registers.v[args[0].toInt()] == args[1].toByte()) {
                    registers.pc = (registers.pc + 2).toShort()
                }
            }
            InstructionSet.SNE_VX_KK -> {
                if (registers.v[args[0].toInt()] != args[1].toByte()) {
                    registers.pc = (registers.pc + 2).toShort()
                }
            }
            else -> {}
        }
    }
}