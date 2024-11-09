package io.github.antoniomayk.jwhisper.jni;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.antoniomayk.jwhisper.utils.AudioUtils;
import io.github.antoniomayk.jwhisper.utils.ResourceLoaderUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Checks integration with JNI and assert corresponding values. */
final class WhisperCppTest {
  private WhisperCppTest() {}

  /** Loads jwhisper library. */
  @BeforeAll
  static void initAll() {
    ResourceLoaderUtils.loadLibrary(WhisperCppTest.class, "linux-x86_64/libjwhisper.so");
  }

  /**
   * Asserts full transcription from jfk.wav.
   *
   * @throws IOException if an I/O error occurs.
   * @throws UnsupportedAudioFileException if the input stream does not match PCMS16 Mono format.
   */
  @Test
  void fullTranscribe() throws IOException, UnsupportedAudioFileException {
    final var pathModel = Paths.get(System.getenv("GGML_MODELS"), "ggml-tiny.bin").toString();
    final var pathWav = ResourceLoaderUtils.getResource(WhisperCppTest.class, "samples/jfk.wav");
    final var whisperContextParams = WhisperCpp.whisperContextDefaultParamsByRef();
    final var whisperContext =
        WhisperCpp.whisperInitFromFileWithParams(pathModel, whisperContextParams);
    final var whisperFullParams = WhisperCpp.whisperFullDefaultParamsByRef(0);
    final var wav = AudioSystem.getAudioInputStream(new File(pathWav));
    final var samples = AudioUtils.convertPcmS16MonoToPcmF32Mono(wav);

    final var errors =
        WhisperCpp.whisperFull(whisperContext, whisperFullParams, samples, samples.length);
    assertThat(errors).isZero();

    final var numberOfSegments = WhisperCpp.whisperFullNumberOfSegments(whisperContext);
    assertThat(numberOfSegments).isEqualTo(1);

    final var segmentText = WhisperCpp.whisperFullGetSegmentText(whisperContext, 0);
    assertThat(
            "And so my fellow Americans ask not what your country can do for you ask what you can"
                + " do for your country")
        .isEqualTo(segmentText.replaceAll("[.,]", "").trim());

    WhisperCpp.whisperFree(whisperContext);
    WhisperCpp.whisperFreeParams(whisperContextParams);
    WhisperCpp.whisperFreeContextParams(whisperFullParams);
  }
}
