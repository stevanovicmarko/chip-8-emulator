const val NUMBER_OF_REGISTERS = 16
const val STACK_DEPTH = 16

class Registers {
    private val vx: Array<Byte> = Array(NUMBER_OF_REGISTERS) { 0 }
    private var vi: Byte = 0
    private var delayTimer: Byte = 0
    private var soundTimer: Byte = 0
    private var pc = LOAD_PROGRAM_ADDRESS
    private var sp: Short = -1
    private var stack: Array<Short> = Array(STACK_DEPTH) { 0 }

    fun reset() {
        vx.fill(0)
        vi = 0
        delayTimer = 0
        soundTimer = 0
        pc = LOAD_PROGRAM_ADDRESS
        sp = -1
        stack.fill(0)
    }

    fun stackPush(value: Short) {
        sp++
        assertStackOverflow()
        stack[sp.toInt()] = value
    }

    fun stackPop(): Short {
        val value = stack[sp.toInt()]
        sp--
        assertStackUnderflow()
        return value
    }

    private fun assertStackUnderflow() {
        assert(sp >= -1) { "Error stack underflow" }
    }

    private fun assertStackOverflow() {
        assert(sp < STACK_DEPTH) { "Error stack overflow" }
    }
}