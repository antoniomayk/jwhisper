package io.github.antoniomayk.jwhisper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.antoniomayk.jwhisper.utils.AudioUtils;
import io.github.antoniomayk.jwhisper.utils.ResourceLoaderUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.junit.jupiter.api.Test;

class JwhisperTest {
  @Test
  void shouldThrowIoExceptionWhenModelFileIsNotReadable() throws IOException {
    final var nonReadableModel = Files.createTempFile("non-readable-model", ".bin");
    nonReadableModel.toFile().setReadable(false);

    assertThatThrownBy(() -> Jwhisper.newInstance(nonReadableModel))
        .isInstanceOf(IOException.class)
        .hasMessage("Model file is not readable: %s", nonReadableModel);

    Files.deleteIfExists(nonReadableModel);
  }

  @Test
  void shouldThrowIoExceptionWhenModelFileDoesNotExist() {
    final var nonExistentPath = Paths.get("/path/to/non/existent/model.bin");

    assertThatThrownBy(() -> Jwhisper.newInstance(nonExistentPath))
        .isInstanceOf(IOException.class)
        .hasMessage("Model file does not exist: %s", nonExistentPath);
  }

  @Test
  void shouldCorrectlyTranscribeAndSegmentAudioFile()
      throws UnsupportedAudioFileException, IOException {
    final var jwhisper =
        Jwhisper.newInstance(Paths.get(System.getenv("GGML_MODELS"), "ggml-tiny.en.bin"));

    final var wavFile = ResourceLoaderUtils.getResource(JwhisperTest.class, "samples/jfk.wav");
    final var wavInputStream = AudioSystem.getAudioInputStream(new File(wavFile));
    final var samples = AudioUtils.convertPcmS16MonoToPcmF32Mono(wavInputStream);

    final var segmentsSize = jwhisper.whisperFull(samples);

    assertThat(segmentsSize).isEqualTo(2);

    assertThat(jwhisper.whisperFullGetSegmentT0(0)).isZero();
    assertThat(jwhisper.whisperFullGetSegmentT1(0)).isEqualTo(800);
    assertThat(jwhisper.whisperFullGetSegmentText(0).trim())
        .isEqualTo("And so my fellow Americans ask not what your country can do for you");

    assertThat(jwhisper.whisperFullGetSegmentT0(1)).isEqualTo(800);
    assertThat(jwhisper.whisperFullGetSegmentT1(1)).isEqualTo(1100);
    assertThat(jwhisper.whisperFullGetSegmentText(1).trim())
        .isEqualTo("ask what you can do for your country.");

    jwhisper.close();
  }

  @Test
  void shouldNotThrowExceptionWhenCallingCloseMultipleTimes() throws IOException {
    final var jwhisper =
        Jwhisper.newInstance(Paths.get(System.getenv("GGML_MODELS"), "ggml-tiny.en.bin"));

    jwhisper.close();
    assertThatCode(jwhisper::close).doesNotThrowAnyException();
    assertThatCode(jwhisper::close).doesNotThrowAnyException();
  }

  @Test
  void shouldThrowIllegalStateExceptionWhenCallingMethodAfterClose() throws IOException {
    final var jwhisper =
        Jwhisper.newInstance(Paths.get(System.getenv("GGML_MODELS"), "ggml-tiny.en.bin"));
    jwhisper.close();

    assertThatThrownBy(() -> jwhisper.whisperFull(new float[0]))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Resource is already closed.");
  }
}
