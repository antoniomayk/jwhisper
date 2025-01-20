package io.github.antoniomayk.jwhisper.jni;

import io.github.antoniomayk.jwhisper.exceptions.WhisperFullException;
import io.github.antoniomayk.jwhisper.utils.LibraryUtils;
import io.github.antoniomayk.jwhisper.utils.ResourceLoaderUtils;

/**
 * JNI class for mapping the <b>whisper.h</b> header file.
 *
 * @author Antonio Mayk
 * @since 0.1
 */
public class WhisperCpp {
  static {
    final var resourcePath = LibraryUtils.getLibraryResourcePath("jwhisper");
    ResourceLoaderUtils.loadLibrary(WhisperCpp.class, resourcePath);
  }

  private WhisperCpp() {}

  /**
   * Function for loading a ggml whisper model. Allocate (almost) all memory needed for the model.
   * Return NULL on failure.
   *
   * <p>From <b>whisper.h</b>
   *
   * <pre>
   * struct whisper_context *
   * whisper_init_from_file_with_params(const char *path_model,
   *                                    struct whisper_context_params params);
   * </pre>
   *
   * @param pathModel model file path
   * @param params whisper_context_params pointer
   * @return a reference pointer to whisper_context
   */
  public static native WhisperContext whisperInitFromFileWithParams(
      String pathModel, WhisperContextParams params);

  /**
   * Frees all allocated memory.
   *
   * <p>From <b>whisper.h</b>
   *
   * <pre>
   * void whisper_free(struct whisper_context *ctx);
   * </pre>
   *
   * @param ctx whisper_context pointer
   */
  public static native void whisperFree(WhisperContext ctx);

  /**
   * Frees all allocated memory.
   *
   * <p>From <b>whisper.h</b>
   *
   * <pre>
   * void whisper_free_params(struct whisper_full_params *params);
   * </pre>
   *
   * @param params whisper_full_params pointer
   */
  public static native void whisperFreeParams(WhisperFullParams params);

  /**
   * Frees all allocated memory.
   *
   * <p>From <b>whisper.h</b>
   *
   * <pre>
   * void whisper_free_context_params(struct whisper_context_params *params);
   * </pre>
   *
   * @param params whisper_context_params pointer
   */
  public static native void whisperFreeContextParams(WhisperContextParams params);

  /**
   * NOTE: this function allocates memory, and it is the responsibility of the caller to free the
   * pointer - see whisper_free_context_params &amp; whisper_free_params().
   *
   * <p>From <b>whisper.h</b>
   *
   * <pre>
   * struct whisper_context_params *whisper_context_default_params_by_ref(void);
   * </pre>
   *
   * @return a reference pointer to whisper_context_params
   */
  public static native WhisperContextParams whisperContextDefaultParamsByRef();

  /**
   * NOTE: this function allocates memory, and it is the responsibility of the caller to free the
   * pointer - see whisper_free_context_params &amp; whisper_free_params().
   *
   * <p>From <b>whisper.h</b>
   *
   * <pre>
   * struct whisper_full_params *
   * whisper_full_default_params_by_ref(enum whisper_sampling_strategy strategy);
   *
   * </pre>
   *
   * @param strategy whisper_sampling_strategy used
   * @return a reference pointer to whisper_full_params
   */
  public static native WhisperFullParams whisperFullDefaultParamsByRef(int strategy);

  /**
   * Run the entire model: PCM - log mel spectrogram - encoder - decoder - text Not thread safe for
   * same context. Uses the specified decoding strategy to obtain the text.
   *
   * <p>From <b>whisper.h</b>
   *
   * <pre>
   * int whisper_full(struct whisper_context *ctx, struct whisper_full_params params,
   *                  const float *samples, int n_samples);
   * </pre>
   *
   * @param ctx whisper_context pointer
   * @param params whisper_full_params pointer
   * @param samples PCM32F audio wave array
   * @param samplesSize amount of frames in the audio wave
   */
  public static native void whisperFull(
      WhisperContext ctx, WhisperFullParams params, float[] samples, int samplesSize)
      throws WhisperFullException;

  /**
   * Number of generated text segments. A segment can be a few words, a sentence, or even a
   * paragraph.
   *
   * <p>From <b>whisper.h</b>
   *
   * <pre>
   * int whisper_full_n_segments(struct whisper_context *ctx);
   * </pre>
   *
   * @param ctx whisper_context pointer
   * @return number of segmens
   */
  public static native int whisperFullNumberOfSegments(WhisperContext ctx);

  /**
   * Get the text of the specified segment.
   *
   * <p>From <b>whisper.h</b>
   *
   * <pre>
   * const char *whisper_full_get_segment_text(struct whisper_context *ctx,
   *                                           int i_segment);
   * </pre>
   *
   * @param ctx whisper_context pointer
   * @param segmentOffset segment number
   * @return the segment text
   */
  public static native String whisperFullGetSegmentText(WhisperContext ctx, int segmentOffset);

  /**
   * Get the start time of the specified segment.
   *
   * <p>From <b>whisper.h</b>
   *
   * <pre>
   * int64_t whisper_full_get_segment_t0(struct whisper_context * ctx, int i_segment);
   * </pre>
   *
   * @param ctx whisper_context pointer
   * @param segmentOffset segment number
   * @return the segment start time
   */
  public static native long whisperFullGetSegmentT0(WhisperContext ctx, int segmentOffset);

  /**
   * Get the end time of the specified segment.
   *
   * <p>From <b>whisper.h</b>
   *
   * <pre>
   * int64_t whisper_full_get_segment_t1(struct whisper_context * ctx, int i_segment);
   * </pre>
   *
   * @param ctx whisper_context pointer
   * @param segmentOffset segment number
   * @return the segment end time
   */
  public static native long whisperFullGetSegmentT1(WhisperContext ctx, int segmentOffset);
}
