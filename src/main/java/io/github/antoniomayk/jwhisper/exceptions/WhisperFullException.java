package io.github.antoniomayk.jwhisper.exceptions;

/**
 * Exception thrown when <b>whisper_full</b> function returns a negative number.
 *
 * <p>From <b>whisper.h</b>
 *
 * <pre>
 * int whisper_full(struct whisper_context *ctx, struct whisper_full_params params,
 *                  const float *samples, int n_samples);
 * </pre>
 */
public class WhisperFullException extends RuntimeException {
  public WhisperFullException(String message) {
    super(message);
  }
}
