const val MEMORY_SIZE = 4095
const val LOAD_PROGRAM_ADDRESS = 0x200

class Memory(val memory: Array<UInt> = Array(MEMORY_SIZE) { 0u }) {
    private fun reset() {
        memory.fill(0u)
    }

    private fun setMemory(index: Int, value: UInt) {
        assertMemory(index)
        memory[index] = value
    }

    private fun getMemory(index: Int): UInt {
        assertMemory(index)
        return memory[index]
    }

    private fun assertMemory(index: Int) {
        print(index in 0 until MEMORY_SIZE)
    }
}