enum class InstructionIdentifier {
    CLS,
    RET,
    JP_ADDR,
    CALL_ADDR,
    SE_VX_KK,
    SE_VX_VY,
    LD_VX_KK,
    ADD_VX_KK,
    LD_VX_VY,
    OR_VX_VY,
    AND_VX_VY,
    XOR_VX_VY,
    ADD_VX_VY,
    SUB_VX_VY,
    SHR_VX_VY,
    SUBN_VX_VY,
    SHL_VX_VY,
    SNE_VX_VY,
    LD_I_ADDR,
    JP_V0_ADDR,
    RND_VX,
    DRW_VX_VY_N,
    SKP_VX,
    SKNP_VX,
    LD_VX_DT,
    LD_VX_K,
    LD_DT_VX,
    LD_ST_VX,
    ADD_I_VX,
    LD_F_VX,
    LD_B_VX,
    LD_I_VX,
    LD_VX_I
}

class Disassembler {
    data class InstructionArg(val mask: Int, val bitsShift: Int? = null)

    data class Instruction(
        val key: Int,
        val id: InstructionIdentifier,
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
        Instruction(2, InstructionIdentifier.CLS, "CLS", 0xFFFF, 0x00E0),
        Instruction(3, InstructionIdentifier.RET, "RET", 0xFFFF, 0x00EE),
        Instruction(
            4, InstructionIdentifier.JP_ADDR, "JP", MASK_HIGHEST_BYTE, 0x1000, listOf(
                MASK_NNN
            )
        ),
        Instruction(
            5, InstructionIdentifier.CALL_ADDR, "CALL", MASK_HIGHEST_BYTE, 0x2000, listOf(
                MASK_NNN
            )
        ),
        Instruction(
            6, InstructionIdentifier.SE_VX_KK, "SE", MASK_HIGHEST_BYTE, 0x3000, listOf(
                MASK_X,
                MASK_KK
            )
        ),
        Instruction(
            7, InstructionIdentifier.SE_VX_KK, "SNE", MASK_HIGHEST_BYTE, 0x4000, listOf(
                MASK_X,
                MASK_KK
            )
        ),
        Instruction(
            8, InstructionIdentifier.SE_VX_VY, "SE", MASK_HIGHEST_AND_LOWEST_BYTE, 0x5000, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            9, InstructionIdentifier.LD_VX_KK, "LD", MASK_HIGHEST_BYTE, 0x6000, listOf(
                MASK_X,
                MASK_KK
            )
        ),
        Instruction(
            10, InstructionIdentifier.ADD_VX_KK, "ADD", MASK_HIGHEST_BYTE, 0x7000, listOf(
                MASK_X,
                MASK_KK
            )
        ),
        Instruction(
            11, InstructionIdentifier.LD_VX_VY, "LD", MASK_HIGHEST_AND_LOWEST_BYTE, 0x8000, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            12, InstructionIdentifier.OR_VX_VY, "OR", MASK_HIGHEST_AND_LOWEST_BYTE, 0x8001, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            13, InstructionIdentifier.AND_VX_VY, "AND", MASK_HIGHEST_AND_LOWEST_BYTE, 0x8002, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            14, InstructionIdentifier.XOR_VX_VY, "XOR", MASK_HIGHEST_AND_LOWEST_BYTE, 0x8003, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            15, InstructionIdentifier.ADD_VX_VY, "ADD", MASK_HIGHEST_AND_LOWEST_BYTE, 0x8004, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            16, InstructionIdentifier.SUB_VX_VY, "SUB", MASK_HIGHEST_AND_LOWEST_BYTE, 0x8005, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            17, InstructionIdentifier.SHR_VX_VY, "SHR", MASK_HIGHEST_AND_LOWEST_BYTE, 0x8006, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            18, InstructionIdentifier.SUBN_VX_VY, "SUBN", MASK_HIGHEST_AND_LOWEST_BYTE, 0x8007, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            19, InstructionIdentifier.SHL_VX_VY, "SHL", MASK_HIGHEST_AND_LOWEST_BYTE, 0x800E, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            20, InstructionIdentifier.SNE_VX_VY, "SNE", MASK_HIGHEST_AND_LOWEST_BYTE, 0x9000, listOf(
                MASK_X,
                MASK_Y
            )
        ),
        Instruction(
            21, InstructionIdentifier.LD_I_ADDR, "LD", MASK_HIGHEST_BYTE, 0xA000, listOf(
                MASK_NNN
            )
        ),
        Instruction(
            22, InstructionIdentifier.JP_V0_ADDR, "JP", MASK_HIGHEST_BYTE, 0xB000, listOf(
                MASK_NNN
            )
        ),
        Instruction(
            23, InstructionIdentifier.RND_VX, "RND", MASK_HIGHEST_BYTE, 0xC000, listOf(
                MASK_X, MASK_KK
            )
        ),
        Instruction(
            24, InstructionIdentifier.DRW_VX_VY_N, "DRW", MASK_HIGHEST_BYTE, 0xD000, listOf(
                MASK_X, MASK_Y, MASK_N
            )
        ),
        Instruction(
            25, InstructionIdentifier.SKP_VX, "SKP", MASK_SECOND_BYTE, 0xE09E, listOf(
                MASK_X
            )
        ),
        Instruction(
            26, InstructionIdentifier.SKNP_VX, "SKNP", MASK_SECOND_BYTE, 0xE0A1, listOf(
                MASK_X
            )
        ),
        Instruction(
            27, InstructionIdentifier.LD_VX_DT, "LD", MASK_SECOND_BYTE, 0xF007, listOf(
                MASK_X
            )
        ),
        Instruction(
            28, InstructionIdentifier.LD_VX_K, "LD", MASK_SECOND_BYTE, 0xF00A, listOf(
                MASK_X
            )
        ),
        Instruction(
            29, InstructionIdentifier.LD_DT_VX, "LD", MASK_SECOND_BYTE, 0xF015, listOf(
                MASK_X
            )
        ),
        Instruction(
            30, InstructionIdentifier.LD_ST_VX, "LD", MASK_SECOND_BYTE, 0xF018, listOf(
                MASK_X
            )
        ),
        Instruction(
            31, InstructionIdentifier.ADD_I_VX, "ADD", MASK_SECOND_BYTE, 0xF01E, listOf(
                MASK_X
            )
        ),
        Instruction(
            32, InstructionIdentifier.LD_F_VX, "LD", MASK_SECOND_BYTE, 0xF029, listOf(
                MASK_X
            )
        ),
        Instruction(
            33, InstructionIdentifier.LD_B_VX, "LD", MASK_SECOND_BYTE, 0xF033, listOf(
                MASK_X
            )
        ),
        Instruction(
            34, InstructionIdentifier.LD_I_VX, "LD", MASK_SECOND_BYTE, 0xF055, listOf(
                MASK_X
            )
        ),
        Instruction(
            35, InstructionIdentifier.LD_VX_I, "LD", MASK_SECOND_BYTE, 0xF065, listOf(
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