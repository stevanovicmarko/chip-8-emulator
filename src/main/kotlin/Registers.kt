const val NUMBER_OF_REGISTERS = 16
const val STACK_DEPTH = 16
const val TIMER_60_HZ = 1000.0 / 60

class Registers {
    val v: Array<UByte> = Array(NUMBER_OF_REGISTERS) { 0u }
    var i: Byte = 0
    var delayTimer: Byte = 0
    var soundTimer: Byte = 0
    var pc = LOAD_PROGRAM_ADDRESS
    var sp: Short = -1
    var stack: Array<Short> = Array(STACK_DEPTH) { 0 }

    fun reset() {
        v.fill(0u)
        i = 0
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