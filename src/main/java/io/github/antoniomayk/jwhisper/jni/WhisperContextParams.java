package io.github.antoniomayk.jwhisper.jni;

/**
 * Represents a Whisper context parameters structure.
 *
 * @author Antonio Mayk
 * @since 0.1
 */
public class WhisperContextParams extends Cpointer {
  public WhisperContextParams(long pointer) {
    super(pointer);
  }
}
