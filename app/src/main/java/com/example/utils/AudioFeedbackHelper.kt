package com.example.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * AudioFeedbackHelper provides subtle, pleasant audio cues during Proof ML Kit OCR
 * text extraction, package scanning, and compliance verification.
 */
object AudioFeedbackHelper {

    private const val TAG = "AudioFeedbackHelper"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Cached synthesized PCM audio buffers for instantaneous, zero-latency playback
    private val successChimePcm: ByteArray by lazy {
        generateHarmonicChimePcm(
            frequencies = doubleArrayOf(1046.50, 1318.51), // C6 -> E6 crisp ascending chime
            sampleRate = 44100,
            durationMs = 120,
            volume = 0.30f
        )
    }

    private val detectionBlipPcm: ByteArray by lazy {
        generateTonePcm(
            frequency = 1174.66, // D6 subtle blip
            sampleRate = 44100,
            durationMs = 50,
            volume = 0.20f
        )
    }

    private val complianceAlertPcm: ByteArray by lazy {
        generateHarmonicChimePcm(
            frequencies = doubleArrayOf(440.0, 349.23), // A4 -> F4 gentle low notification
            sampleRate = 44100,
            durationMs = 150,
            volume = 0.35f
        )
    }

    /**
     * Plays a crisp, subtle confirmation chime when Proof ML Kit successfully extracts
     * text, weight, volume, MRP, or packaging declarations from a scanned package.
     */
    fun playOcrSuccessSound(context: Context? = null) {
        scope.launch {
            try {
                playPcmBuffer(successChimePcm, sampleRate = 44100)
            } catch (e: Exception) {
                Log.w(TAG, "Custom PCM chime failed, attempting ToneGenerator fallback: ${e.message}")
                playToneGeneratorFallback(ToneGenerator.TONE_PROP_BEEP, 80)
            }
        }
    }

    /**
     * Plays a short, ultra-subtle blip when real-time OCR detects a specific marking
     * (e.g. Weight, Volume, or Barcode) in live camera viewfinder.
     */
    fun playScanDetectionBlip(context: Context? = null) {
        scope.launch {
            try {
                playPcmBuffer(detectionBlipPcm, sampleRate = 44100)
            } catch (e: Exception) {
                playToneGeneratorFallback(ToneGenerator.TONE_CDMA_PIP, 40)
            }
        }
    }

    /**
     * Plays a gentle alert tone when non-compliance or missing mandatory declarations
     * are identified on the scanned package.
     */
    fun playComplianceAlertSound(context: Context? = null) {
        scope.launch {
            try {
                playPcmBuffer(complianceAlertPcm, sampleRate = 44100)
            } catch (e: Exception) {
                playToneGeneratorFallback(ToneGenerator.TONE_PROP_NACK, 120)
            }
        }
    }

    /**
     * Plays raw 16-bit Mono PCM audio through AudioTrack with smooth envelope.
     */
    private fun playPcmBuffer(pcmData: ByteArray, sampleRate: Int) {
        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSize = maxOf(minBufferSize, pcmData.size)

        val audioTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()
        } else {
            @Suppress("DEPRECATION")
            AudioTrack(
                AudioManager.STREAM_NOTIFICATION,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STATIC
            )
        }

        try {
            audioTrack.write(pcmData, 0, pcmData.size)
            audioTrack.play()
            // Schedule cleanup after playback duration + margin
            Thread.sleep((pcmData.size * 1000L) / (sampleRate * 2) + 50)
        } finally {
            try {
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {}
        }
    }

    /**
     * Generates a smooth two-tone harmonic ascending/descending chime with Hanning/cosine envelope
     * to eliminate audio clicks, pops, and harshness.
     */
    private fun generateHarmonicChimePcm(
        frequencies: DoubleArray,
        sampleRate: Int,
        durationMs: Int,
        volume: Float
    ): ByteArray {
        val totalSamples = (sampleRate * durationMs) / 1000
        val pcm = ByteArray(totalSamples * 2) // 16-bit mono = 2 bytes per sample
        val halfSamples = totalSamples / frequencies.size

        for (i in 0 until totalSamples) {
            val freqIndex = (i / halfSamples).coerceIn(0, frequencies.size - 1)
            val freq = frequencies[freqIndex]

            // Angle for sine wave
            val angle = 2.0 * PI * i * freq / sampleRate
            var sample = sin(angle)

            // Apply smooth envelope: linear attack + exponential decay
            val progress = i.toDouble() / totalSamples
            val envelope = when {
                progress < 0.15 -> progress / 0.15 // Fast smooth fade-in
                else -> kotlin.math.exp(-3.5 * (progress - 0.15)) // Pleasant decay
            }

            val finalSampleVal = (sample * envelope * volume * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()

            // Little-endian 16-bit PCM
            pcm[i * 2] = (finalSampleVal.toInt() and 0xFF).toByte()
            pcm[i * 2 + 1] = ((finalSampleVal.toInt() shr 8) and 0xFF).toByte()
        }
        return pcm
    }

    /**
     * Generates a single-frequency subtle tone with fast envelope.
     */
    private fun generateTonePcm(
        frequency: Double,
        sampleRate: Int,
        durationMs: Int,
        volume: Float
    ): ByteArray {
        val totalSamples = (sampleRate * durationMs) / 1000
        val pcm = ByteArray(totalSamples * 2)

        for (i in 0 until totalSamples) {
            val angle = 2.0 * PI * i * frequency / sampleRate
            val sample = sin(angle)
            val progress = i.toDouble() / totalSamples
            val envelope = when {
                progress < 0.2 -> progress / 0.2
                else -> kotlin.math.exp(-4.0 * (progress - 0.2))
            }

            val finalSampleVal = (sample * envelope * volume * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()

            pcm[i * 2] = (finalSampleVal.toInt() and 0xFF).toByte()
            pcm[i * 2 + 1] = ((finalSampleVal.toInt() shr 8) and 0xFF).toByte()
        }
        return pcm
    }

    /**
     * Fallback to ToneGenerator if system AudioTrack is unavailable.
     */
    private fun playToneGeneratorFallback(toneType: Int, durationMs: Int) {
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 35)
            toneGen.startTone(toneType, durationMs)
            Thread.sleep(durationMs.toLong() + 20)
            toneGen.release()
        } catch (e: Exception) {
            Log.w(TAG, "ToneGenerator fallback error: ${e.message}")
        }
    }
}
