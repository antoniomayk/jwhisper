package io.github.antoniomayk.jwhisper.jni;

/**
 * Represents a Whisper full parameters structure.
 *
 * @author Antonio Mayk
 * @since 0.1
 */
public class WhisperFullParams extends Cpointer {
  public WhisperFullParams(long pointer) {
    super(pointer);
  }
}
