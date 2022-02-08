const val MEMORY_SIZE = 4095
const val LOAD_PROGRAM_ADDRESS = 0x200
const val CHAR_SET_ADDRESS = 0x000
const val CHAR_SET_WIDTH = 8

class Memory(val memory: Array<UByte> = Array(MEMORY_SIZE) { 0u }) {
    private fun reset() {
        memory.fill(0u)
    }

    fun setMemory(index: Int, value: UByte) {
        assertMemory(index)
        memory[index] = value
    }

    fun getMemory(index: Int): UByte {
        assertMemory(index)
        return memory[index]
    }

    fun getOpcode(index: Int): UShort {
        val highByte = getMemory(index)
        val lowByte = getMemory(index + 1)
        return (highByte * 256u).toUShort() or lowByte.toUShort()
    }

    private fun assertMemory(index: Int) {
        assert(index in 0 until MEMORY_SIZE) {"Illegal memory access"}
    }
}