import javafx.scene.canvas.Canvas
import kotlinx.coroutines.delay
import java.io.File
import kotlin.math.floor
import kotlin.random.Random

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
                if (registers.v[args[0].toInt()] == args[1].toUByte()) {
                    registers.pc = (registers.pc + 2).toShort()
                }
            }
            InstructionSet.SNE_VX_KK -> {
                if (registers.v[args[0].toInt()] != args[1].toUByte()) {
                    registers.pc = (registers.pc + 2).toShort()
                }
            }
            InstructionSet.SE_VX_VY -> {
                if (registers.v[args[0].toInt()] == registers.v[args[1].toInt()]) {
                    registers.pc = (registers.pc + 2).toShort()
                }
            }
            InstructionSet.LD_VX_KK -> {
                registers.v[args[0].toInt()] = args[1].toUByte()
            }
            InstructionSet.ADD_VX_KK -> {
                val index = args[0].toInt()
                registers.v[index] = (registers.v[index] + args[1].toUByte()).toUByte()
            }
            InstructionSet.LD_VX_VY -> {
                registers.v[args[0].toInt()] = registers.v[args[1].toInt()]
            }
            InstructionSet.OR_VX_VY -> {
                val (xIndex, yIndex) = extractIndices(args[0], args[1])
                registers.v[xIndex] = registers.v[xIndex] or registers.v[yIndex]
            }
            InstructionSet.AND_VX_VY -> {
                val (xIndex, yIndex) = extractIndices(args[0], args[1])
                registers.v[xIndex] = registers.v[xIndex] and registers.v[yIndex]
            }
            InstructionSet.XOR_VX_VY -> {
                val (xIndex, yIndex) = extractIndices(args[0], args[1])
                registers.v[xIndex] = registers.v[xIndex] xor registers.v[yIndex]
            }
            InstructionSet.ADD_VX_VY -> {
                val (xIndex, yIndex) = extractIndices(args[0], args[1])
                registers.v[0x0f] = if (registers.v[xIndex] + registers.v[yIndex] > 0xffu) 1u else 0u
                registers.v[xIndex] = (registers.v[xIndex] + registers.v[yIndex]).toUByte()
            }
            InstructionSet.SUB_VX_VY -> {
                val (xIndex, yIndex) = extractIndices(args[0], args[1])
                registers.v[0x0f] = if (registers.v[xIndex] > registers.v[yIndex]) 1u else 0u
                registers.v[xIndex] = (registers.v[xIndex] - registers.v[yIndex]).toUByte()
            }
            InstructionSet.SHR_VX_VY -> {
                val xIndex = args[0].toInt()
                registers.v[0x0f] = registers.v[xIndex] and 0x01u
                registers.v[xIndex] = (registers.v[xIndex].toInt() shr 1).toUByte()
            }
            InstructionSet.SUBN_VX_VY -> {
                val (xIndex, yIndex) = extractIndices(*args.toShortArray())
                registers.v[0x0f] = if (registers.v[yIndex] > registers.v[xIndex]) 1u else 0u
                registers.v[xIndex] = (registers.v[yIndex] - registers.v[xIndex]).toUByte()
            }
            InstructionSet.SHL_VX_VY -> {
                val (xIndex) = extractIndices(*args.toShortArray())
                registers.v[0x0f] = if ((registers.v[xIndex] and 0x80u) != 0u.toUByte()) 1u else 0u
                registers.v[xIndex] = (registers.v[xIndex].toInt() shl 1).toUByte()
            }
            InstructionSet.SNE_VX_VY -> {
                val (xIndex, yIndex) = extractIndices(args[0], args[1])
                if (registers.v[xIndex] != registers.v[yIndex]) {
                    registers.pc = (registers.pc + 2).toShort()
                }
            }
            InstructionSet.LD_I_ADDR -> {
                registers.i = args[0].toUShort()
            }
            InstructionSet.JP_V0_ADDR -> {
                registers.pc = (registers.v[0].toShort() + args[0]).toShort()
            }
            InstructionSet.RND_VX -> {
                val (xIndex) = extractIndices(args[0])
                val random = floor(Random.nextDouble() * 0xff).toInt()
                registers.v[xIndex] = (random and args[1].toInt()).toUByte()
            }
            InstructionSet.DRW_VX_VY_N -> {
                val (xIndex, yIndex) = extractIndices(args[0], args[1])
                registers.v[0x0f] = display.drawSprite(
                    registers.v[xIndex].toInt(),
                    registers.v[yIndex].toInt(),
                    registers.i.toInt(),
                    args[2].toInt()
                )
            }
            else -> {}
        }
    }

    private fun extractIndices(vararg args: Short): List<Int> = args.map { it.toInt() }
}