package io.github.antoniomayk.jwhisper.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.withPrecision;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.junit.jupiter.api.Test;

/** Checks integrity of {@link AudioUtils} methods. */
final class AudioUtilsTest {
  private AudioUtilsTest() {}

  /** Asserts that audio format matches to PCMS16 Mono. */
  @Test
  void assertIsPcmS16Mono() {
    final var encoding = AudioFormat.Encoding.PCM_SIGNED;
    final var pcmS16Format = new AudioFormat(encoding, 16000, 16, 1, 2, 16000, false);

    assertThat(AudioUtils.isPcmS16Mono(pcmS16Format)).isTrue();
  }

  /** Asserts that audio format does not match to PCMS16 Mono. */
  @Test
  void assertIsNotPcmS16Mono() {
    var format = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, 16000, 16, 1, 2, 16000, false);
    assertThat(AudioUtils.isPcmS16Mono(format)).isFalse();

    format = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, 16000, 16, 1, 2, 16000, false);
    assertThat(AudioUtils.isPcmS16Mono(format)).isFalse();

    format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 32000, 16, 1, 2, 16000, false);
    assertThat(AudioUtils.isPcmS16Mono(format)).isFalse();

    format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 32, 1, 2, 16000, false);
    assertThat(AudioUtils.isPcmS16Mono(format)).isFalse();

    format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 2, 2, 16000, false);
    assertThat(AudioUtils.isPcmS16Mono(format)).isFalse();

    format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 1, 4, 16000, false);
    assertThat(AudioUtils.isPcmS16Mono(format)).isFalse();

    format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 1, 2, 32000, false);
    assertThat(AudioUtils.isPcmS16Mono(format)).isFalse();

    format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 1, 2, 16000, true);
    assertThat(AudioUtils.isPcmS16Mono(format)).isFalse();
  }

  /**
   * Asserts that PCMS16 Mono conversion to PCMF32 Mono will happen successfully.
   *
   * @throws IOException if an I/O error occurs.
   * @throws UnsupportedAudioFileException if the input stream does not match PCMS16 Mono format.
   */
  @Test
  void assertConvertPcmS16ToPcmF32() throws IOException, UnsupportedAudioFileException {
    final var encoding = AudioFormat.Encoding.PCM_SIGNED;
    final var audioFormat = new AudioFormat(encoding, 16000, 16, 1, 2, 16000, false);
    final var audioData = new byte[] {2, 3, 4, 3};
    final var byteArrayInputStream = new ByteArrayInputStream(audioData);
    final var audioInputStream =
        new AudioInputStream(
            byteArrayInputStream, audioFormat, audioData.length / audioFormat.getFrameSize());
    final var data = AudioUtils.convertPcmS16MonoToPcmF32Mono(audioInputStream);

    assertThat(data).containsExactly(new float[] {0.023499252f, 0.02356029f}, withPrecision(0.0f));
  }

  /** Asserts that different audio format should not be converted to PCMF32 Mono. */
  @Test
  void assertThrowWhenConvertingUnsupportedFormat() {
    final var encoding = AudioFormat.Encoding.PCM_SIGNED;
    final var audioFormat = new AudioFormat(encoding, 32000, 32, 1, 4, 32000, false);
    final var audioData = new byte[] {2, 3, 4, 3};
    final var byteArrayInputStream = new ByteArrayInputStream(audioData);
    final var audioInputStream =
        new AudioInputStream(
            byteArrayInputStream, audioFormat, audioData.length / audioFormat.getFrameSize());

    assertThatThrownBy(() -> AudioUtils.convertPcmS16MonoToPcmF32Mono(audioInputStream))
        .isInstanceOf(UnsupportedAudioFileException.class)
        .hasMessage("The file should consist of PCMS16 wav format.");
  }
}
