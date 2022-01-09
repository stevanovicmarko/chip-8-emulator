class Chip8(width: Int, height: Int) {
    val display = Display(width, height)
    val memory = Memory()
    val registers = Registers()
}