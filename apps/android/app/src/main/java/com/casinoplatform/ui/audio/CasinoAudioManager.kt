package com.casinoplatform.ui.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

object CasinoAudioManager {
    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val audioScope = CoroutineScope(Dispatchers.Default)

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }

    fun setMuted(muted: Boolean) {
        _isMuted.value = muted
    }

    /**
     * Plays a crisp tactile click for bet chips and navigation buttons
     */
    fun playClick() {
        if (_isMuted.value) return
        audioScope.launch {
            synthesizeTone(frequency = 1200.0, durationMs = 15, volume = 0.5f, decay = true)
        }
    }

    /**
     * High-dopamine casino coin cashout chime ("Ka-ching")
     */
    fun playCashout() {
        if (_isMuted.value) return
        audioScope.launch {
            // Arpeggiated golden chord (1760 Hz + 2637 Hz)
            synthesizeChord(
                frequencies = doubleArrayOf(1760.0, 2637.0),
                durationMs = 280,
                volume = 0.8f
            )
        }
    }

    /**
     * Heavy low-frequency sub-bass crash explosion
     */
    fun playCrash() {
        if (_isMuted.value) return
        audioScope.launch {
            synthesizeCrashNoise(durationMs = 350, volume = 0.9f)
        }
    }

    /**
     * Sparkling crystal bell for uncovering a gem in Mines
     */
    fun playGemReveal() {
        if (_isMuted.value) return
        audioScope.launch {
            synthesizeTone(frequency = 2093.0, durationMs = 160, volume = 0.7f, decay = true)
        }
    }

    /**
     * Crisp percussive pin blip for Plinko & Neon Bounce
     */
    fun playBounce() {
        if (_isMuted.value) return
        audioScope.launch {
            synthesizeTone(frequency = 880.0, durationMs = 25, volume = 0.6f, decay = true)
        }
    }

    /**
     * Spring-loaded hop tone for Frog Frenzy
     */
    fun playHop() {
        if (_isMuted.value) return
        audioScope.launch {
            synthesizeFrequencyGlide(startFreq = 400.0, endFreq = 950.0, durationMs = 80, volume = 0.6f)
        }
    }

    /**
     * Deep ocean sonar ping for Deep Dive
     */
    fun playSonar() {
        if (_isMuted.value) return
        audioScope.launch {
            synthesizeTone(frequency = 1400.0, durationMs = 300, volume = 0.5f, decay = true)
        }
    }

    /**
     * Elevator floor chime for Sky Elevator
     */
    fun playElevatorDing() {
        if (_isMuted.value) return
        audioScope.launch {
            synthesizeTone(frequency = 1046.5, durationMs = 220, volume = 0.7f, decay = true)
        }
    }

    // --- Audio Synthesis Utilities (Self-Contained PCM Generation) ---

    private fun synthesizeTone(
        frequency: Double,
        durationMs: Int,
        volume: Float,
        decay: Boolean = false
    ) {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = if (decay) (1.0 - (i.toDouble() / numSamples)).coerceIn(0.0, 1.0) else 1.0
            val sample = (sin(2.0 * PI * frequency * t) * envelope * volume * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        writeAndPlayBuffer(buffer, sampleRate)
    }

    private fun synthesizeChord(
        frequencies: DoubleArray,
        durationMs: Int,
        volume: Float
    ) {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = (1.0 - (i.toDouble() / numSamples)).coerceIn(0.0, 1.0)
            var mixed = 0.0
            for (f in frequencies) {
                mixed += sin(2.0 * PI * f * t)
            }
            mixed /= frequencies.size
            val sample = (mixed * envelope * volume * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        writeAndPlayBuffer(buffer, sampleRate)
    }

    private fun synthesizeFrequencyGlide(
        startFreq: Double,
        endFreq: Double,
        durationMs: Int,
        volume: Float
    ) {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val currentFreq = startFreq + (endFreq - startFreq) * progress
            val t = i.toDouble() / sampleRate
            val envelope = (1.0 - progress).coerceIn(0.0, 1.0)
            val sample = (sin(2.0 * PI * currentFreq * t) * envelope * volume * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        writeAndPlayBuffer(buffer, sampleRate)
    }

    private fun synthesizeCrashNoise(
        durationMs: Int,
        volume: Float
    ) {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
        val buffer = ShortArray(numSamples)

        var lastRandom = 0.0
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val envelope = (1.0 - progress).coerceIn(0.0, 1.0)
            // Low-pass filtered noise + sub-bass 55Hz drop
            val t = i.toDouble() / sampleRate
            val subBass = sin(2.0 * PI * (60.0 - 30.0 * progress) * t) * 0.7
            val rawNoise = (Math.random() * 2.0 - 1.0)
            lastRandom = lastRandom * 0.85 + rawNoise * 0.15
            val mixed = (subBass + lastRandom * 0.4) * envelope * volume
            val sample = (mixed * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        writeAndPlayBuffer(buffer, sampleRate)
    }

    private fun writeAndPlayBuffer(buffer: ShortArray, sampleRate: Int) {
        try {
            val minBufSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val trackSize = buffer.size.coerceAtLeast(minBufSize)

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .build()
                )
                .setBufferSizeInBytes(trackSize * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()

            // Release track after playback completes
            audioScope.launch {
                val delayTime = (buffer.size * 1000L / sampleRate) + 50L
                kotlinx.coroutines.delay(delayTime)
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            // Fail gracefully if audio device is unavailable
        }
    }
}
