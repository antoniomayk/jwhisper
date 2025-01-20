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

class AudioUtilsTest {
  @Test
  void shouldBePcmS16Mono() {
    final var encoding = AudioFormat.Encoding.PCM_SIGNED;
    final var pcmS16Format = new AudioFormat(encoding, 16000, 16, 1, 2, 16000, false);

    assertThat(AudioUtils.isPcmS16Mono(pcmS16Format)).isTrue();
  }

  @Test
  void shouldNotBePcmS16Mono() {
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

  @Test
  void shouldConvertPcmS16ToPcmF32() throws IOException, UnsupportedAudioFileException {
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

  @Test
  void shouldThrowWhenConvertingUnsupportedFormat() {
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
