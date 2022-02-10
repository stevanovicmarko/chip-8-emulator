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
    private val MASK_N = InstructionArg(0x000F)
    private val MASK_HIGHEST_BYTE = 0xF000
    private val MASK_HIGHEST_AND_LOWEST_BYTE = 0xF00F
    private val MASK_SECOND_BYTE = 0xF0FF

    private val instructionSet = arrayOf(
        Instruction(2, "CLS", "CLS", 0xFFFF, 0x00E0),
        Instruction(3, "RET", "RET", 0xFFFF, 0x00EE),
        Instruction(
            4, "JP_ADDR", "JP", MASK_HIGHEST_BYTE, 0x1000, listOf(
                MASK_NNN
            )
        ),
        Instruction(
            5, "CALL_ADDR", "CALL", MASK_HIGHEST_BYTE, 0x2000, listOf(
                MASK_NNN
            )
        ),
        Instruction(
            6, "SE_VX_KK", "SE", MASK_HIGHEST_BYTE, 0x3000, listOf(
                MASK_X,
                MASK_KK
            )
        ),
        Instruction(
            7, "SE_VX_KK", "SNE", MASK_HIGHEST_BYTE, 0x4000, listOf(
                MASK_X,
                MASK_KK
            )
        ),
        Instruction(
            8, "SE_VX_VY", "SE", MASK_HIGHEST_AND_LOWEST_BYTE, 0x5000, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            9, "LD_VX_KK", "LD", MASK_HIGHEST_BYTE, 0x6000, listOf(
                MASK_X,
                MASK_KK
            )
        ),
        Instruction(
            10, "ADD_VX_KK", "ADD", MASK_HIGHEST_BYTE, 0x7000, listOf(
                MASK_X,
                MASK_KK
            )
        ),
        Instruction(
            11, "LD_VX_VY", "LD", MASK_HIGHEST_AND_LOWEST_BYTE, 0x8000, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            12, "OR_VX_VY", "OR", MASK_HIGHEST_AND_LOWEST_BYTE, 0x8001, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            13, "AND_VX_VY", "AND", MASK_HIGHEST_AND_LOWEST_BYTE, 0x8002, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            14, "XOR_VX_VY", "XOR", MASK_HIGHEST_AND_LOWEST_BYTE, 0x8003, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            15, "ADD_VX_VY", "ADD", MASK_HIGHEST_AND_LOWEST_BYTE, 0x8004, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            16, "SUB_VX_VY", "SUB", MASK_HIGHEST_AND_LOWEST_BYTE, 0x8005, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            17, "SHR_VX_VY", "SHR", MASK_HIGHEST_AND_LOWEST_BYTE, 0x8006, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            18, "SUBN_VX_VY", "SUBN", MASK_HIGHEST_AND_LOWEST_BYTE, 0x8007, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            19, "SHL_VX_VY", "SHL", MASK_HIGHEST_AND_LOWEST_BYTE, 0x800E, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            20, "SNE_VX_VY", "SNE", MASK_HIGHEST_AND_LOWEST_BYTE, 0x9000, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            21, "LD_I_ADDR", "LD", MASK_HIGHEST_BYTE, 0xA000, listOf(
                MASK_NNN
            )
        ),
        Instruction(
            22, "JP_V0_ADDR", "JP", MASK_HIGHEST_BYTE, 0xB000, listOf(
                MASK_NNN
            )
        ),
        Instruction(
            23, "RND_VX", "RND", MASK_HIGHEST_BYTE, 0xC000, listOf(
                MASK_X, MASK_KK
            )
        ),
        Instruction(
            24, "DRW_VX_VY_N", "DRW", MASK_HIGHEST_BYTE, 0xD000, listOf(
                MASK_X, MASK_Y, MASK_N
            )
        ),
        Instruction(
            25, "SKP_VX", "SKP", MASK_SECOND_BYTE, 0xE09E, listOf(
                MASK_X
            )
        ),
        Instruction(
            26, "SKNP_VX", "SKNP", MASK_SECOND_BYTE, 0xE0A1, listOf(
                MASK_X
            )
        ),
        Instruction(
            27, "LD_VX_DT", "LD", MASK_SECOND_BYTE, 0xF007, listOf(
                MASK_X
            )
        ),
        Instruction(
            28, "LD_VX_K", "LD", MASK_SECOND_BYTE, 0xF00A, listOf(
                MASK_X
            )
        ),
        Instruction(
            29, "LD_DT_VX", "LD", MASK_SECOND_BYTE, 0xF015, listOf(
                MASK_X
            )
        ),
        Instruction(
            30, "LD_ST_VX", "LD", MASK_SECOND_BYTE, 0xF018, listOf(
                MASK_X
            )
        ),
        Instruction(
            31, "ADD_I_VX", "ADD", MASK_SECOND_BYTE, 0xF01E, listOf(
                MASK_X
            )
        ),
        Instruction(
            32, "LD_F_VX", "LD", MASK_SECOND_BYTE, 0xF029, listOf(
                MASK_X
            )
        ),
        Instruction(
            33, "LD_B_VX", "LD", MASK_SECOND_BYTE, 0xF033, listOf(
                MASK_X
            )
        ),
        Instruction(
            34, "LD_I_VX", "LD", MASK_SECOND_BYTE, 0xF055, listOf(
                MASK_X
            )
        ),
        Instruction(
            35, "LD_VX_I", "LD", MASK_SECOND_BYTE, 0xF065, listOf(
                MASK_X
            )
        ),
    )

    fun disassemble(opcode: Int): Pair<Instruction, List<Int>?> {
        val instruction = instructionSet.find {
            (it.mask and opcode) == it.pattern
        } ?: error("Instruction not found")
        val args = instruction.arguments?.map {
            (it.mask and opcode) shr (it.bitsShift ?: 0)
        }
        return Pair(instruction, args)
    }
}