package io.github.antoniomayk.jwhisper.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.jspecify.annotations.NonNull;

/**
 * Utility class for basic audio conversions.
 *
 * @author Antonio Mayk
 * @since 0.1
 */
public class AudioUtils {
  private static final AudioFormat PCM_F32_MONO_FORMAT =
      new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, 16000, 32, 1, 4, 16000, false);

  private AudioUtils() {}

  /**
   * Checks if input format matches PCMS16 Mono.
   *
   * @param audioFormat input to be verified.
   * @return true if all conditions matches.
   */
  public static boolean isPcmS16Mono(@NonNull final AudioFormat audioFormat) {
    return audioFormat.getFrameRate() == 16000
        && audioFormat.getFrameSize() == 2
        && audioFormat.getSampleRate() == 16000
        && audioFormat.getSampleSizeInBits() == 16
        && !audioFormat.isBigEndian()
        && audioFormat.getChannels() == 1
        && Objects.equals(audioFormat.getEncoding(), AudioFormat.Encoding.PCM_SIGNED);
  }

  /**
   * Converts a PCMS16 Mono stream to an array of PCMF32 Mono.
   *
   * <p>NOTE: Use carefully since it will allocate all the sample data.
   *
   * @param pcmS16Stream audio input stream to be converted.
   * @return an array of PCM32F Mono.
   * @throws IOException if an I/O error occurs.
   * @throws UnsupportedAudioFileException if the input stream does not match PCMS16 Mono format.
   */
  public static float[] convertPcmS16MonoToPcmF32Mono(@NonNull AudioInputStream pcmS16Stream)
      throws IOException, UnsupportedAudioFileException {
    if (!isPcmS16Mono(pcmS16Stream.getFormat())) {
      throw new UnsupportedAudioFileException("The file should consist of PCMS16 wav format.");
    }

    final var pcmF32Stream = AudioSystem.getAudioInputStream(PCM_F32_MONO_FORMAT, pcmS16Stream);
    final var pcmF32Bytes = pcmF32Stream.readAllBytes();
    final var pcmF32ByteBuffer = ByteBuffer.wrap(pcmF32Bytes).order(ByteOrder.LITTLE_ENDIAN);
    final var pcmF32Frames = pcmF32Bytes.length / 4;
    final var pcmF32Array = new float[pcmF32Frames];

    for (var i = 0; i < pcmF32Frames; i++) {
      pcmF32Array[i] = pcmF32ByteBuffer.getFloat();
    }

    pcmF32Stream.close();
    pcmS16Stream.close();

    return pcmF32Array;
  }
}
