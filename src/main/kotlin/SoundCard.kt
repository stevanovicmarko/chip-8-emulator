import javafx.scene.media.AudioClip

class SoundCard {
    private var isSoundRunning = false
    private val sound = AudioClip((object {}.javaClass.getResource("beep.mp3")?.toURI() ?: error("Invalid path")).toString())

    init {
        sound.volume = 0.3
        sound.cycleCount = AudioClip.INDEFINITE
    }

    var soundEnabled: Boolean
        get() = isSoundRunning
        set(value) {
            if (value == isSoundRunning) {
                return
            }
            isSoundRunning = value
            when (isSoundRunning) {
                true -> sound.play()
                false -> sound.stop()
            }
        }

}