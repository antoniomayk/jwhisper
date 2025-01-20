package io.github.antoniomayk.jwhisper;

import io.github.antoniomayk.jwhisper.jni.WhisperContext;
import io.github.antoniomayk.jwhisper.jni.WhisperContextParams;
import io.github.antoniomayk.jwhisper.jni.WhisperCpp;
import io.github.antoniomayk.jwhisper.jni.WhisperFullParams;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Jwhisper is a Java wrapper for interacting with the WhisperCpp library. It provides methods to
 * load a model, process audio samples, and retrieve transcription segments.
 *
 * @author Antonio Mayk
 * @since 0.1
 */
public class Jwhisper implements AutoCloseable {

  private final WhisperContext whisperContextPointer;
  private final WhisperContextParams whisperContextParamsPointer;
  private final WhisperFullParams whisperFullParamsPointer;

  private boolean isClosed = false;

  private Jwhisper(final String model) {
    whisperFullParamsPointer = WhisperCpp.whisperFullDefaultParamsByRef(0);
    whisperContextParamsPointer = WhisperCpp.whisperContextDefaultParamsByRef();
    whisperContextPointer =
        WhisperCpp.whisperInitFromFileWithParams(model, whisperContextParamsPointer);
  }

  /**
   * Initializes a Jwhisper instance by loading a GGML model file and configuring default
   * parameters.
   *
   * @param ggmlModel the path to the GGML model file used for transcription.
   * @throws IOException if the model file does not exist or is not readable.
   */
  public static Jwhisper newInstance(final Path ggmlModel) throws IOException {
    if (!Files.exists(ggmlModel)) {
      throw new IOException("Model file does not exist: " + ggmlModel);
    }

    if (!Files.isReadable(ggmlModel)) {
      throw new IOException("Model file is not readable: " + ggmlModel);
    }

    return new Jwhisper(ggmlModel.toString());
  }

  /**
   * Processes audio samples using the loaded model and returns the number of transcription
   * segments.
   *
   * @param samples an array of audio samples to be processed.
   * @return the number of transcription segments generated.
   * @throws RuntimeException if processing the audio fails.
   */
  public int whisperFull(final float[] samples) {
    ensureNotClosed();

    WhisperCpp.whisperFull(
        whisperContextPointer, whisperFullParamsPointer, samples, samples.length);

    return WhisperCpp.whisperFullNumberOfSegments(whisperContextPointer);
  }

  /**
   * Retrieves the text of a specific transcription segment.
   *
   * @param segmentIndex the index of the transcription segment.
   * @return the transcribed text for the specified segment.
   */
  public String whisperFullGetSegmentText(final int segmentIndex) {
    ensureNotClosed();

    return WhisperCpp.whisperFullGetSegmentText(whisperContextPointer, segmentIndex);
  }

  /**
   * Retrieves the start timestamp (T0) of a specific transcription segment in milliseconds.
   *
   * @param segmentIndex the index of the transcription segment.
   * @return the start timestamp (T0) for the specified segment.
   */
  public long whisperFullGetSegmentT0(final int segmentIndex) {
    ensureNotClosed();

    return WhisperCpp.whisperFullGetSegmentT0(whisperContextPointer, segmentIndex);
  }

  /**
   * Retrieves the end timestamp (T1) of a specific transcription segment in milliseconds.
   *
   * @param segmentIndex the index of the transcription segment.
   * @return the end timestamp (T1) for the specified segment.
   */
  public long whisperFullGetSegmentT1(final int segmentIndex) {
    ensureNotClosed();

    return WhisperCpp.whisperFullGetSegmentT1(whisperContextPointer, segmentIndex);
  }

  /**
   * Frees up resources associated with this Jwhisper instance. This includes the model, context
   * parameters, and other resources.
   */
  @Override
  public void close() {
    if (!isClosed) {
      WhisperCpp.whisperFreeParams(whisperFullParamsPointer);
      WhisperCpp.whisperFreeContextParams(whisperContextParamsPointer);
      WhisperCpp.whisperFree(whisperContextPointer);

      isClosed = true;
    }
  }

  private void ensureNotClosed() {
    if (isClosed) {
      throw new IllegalStateException("Resource is already closed.");
    }
  }
}
