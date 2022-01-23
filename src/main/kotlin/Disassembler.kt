class Disassembler {
    data class InstructionArg(val mask: Int, val bitsShift: Int? = null)
    data class Instruction(
        val key: Int,
        val id: String,
        val name: String,
        val mask: Int,
        val pattern: Int,
        val arguments: List<InstructionArg>? = null
    )

    private val MASK_NNN = InstructionArg(0x0FFF)
    private val MASK_X = InstructionArg(0x0F00, 8)
    private val MASK_Y = InstructionArg(0x00F0, 4)
    private val MASK_KK = InstructionArg(0x00FF)

    private val instructionSet = arrayOf(
        Instruction(2, "CLS", "CLS", 0xFFFF, 0x00E0),
        Instruction(3, "RET", "RET", 0xFFFF, 0x00EE),
        Instruction(
            4, "JP_ADDR", "JP", 0xF000, 0x1000, listOf(
                MASK_NNN
            )
        ),
        Instruction(
            5, "CALL_ADDR", "CALL", 0xF000, 0x2000, listOf(
                MASK_NNN
            )
        ),
        Instruction(
            6, "SE_VX_NN", "SE", 0xF000, 0x3000, listOf(
                MASK_X,
                MASK_KK
            )
        ),
        Instruction(
            7, "SE_VX_NN", "SNE", 0xF000, 0x4000, listOf(
                MASK_X,
                MASK_KK
            )
        ),
        Instruction(
            8, "SE_VX_VY", "SE", 0xF00F, 0x5000, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            9, "LD_VX_KK", "LD", 0xF000, 0x6000, listOf(
                MASK_X,
                MASK_KK
            )
        )
    )

    fun disassemble(opcode: Int) {
        val instruction = instructionSet.find {
            (it.mask and opcode) == it.pattern
        } ?: error("Instruction not found")
        println(instruction)
        val args = instruction.arguments?.map {
            (it.mask and opcode) shr (it.bitsShift ?: 0)
        }
        println(args)
    }
}