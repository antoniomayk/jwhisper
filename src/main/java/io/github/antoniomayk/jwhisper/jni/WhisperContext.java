package io.github.antoniomayk.jwhisper.jni;

/**
 * Represents a Whisper context.
 *
 * @author Antonio Mayk
 * @since 0.1
 */
public class WhisperContext extends Cpointer {
  public WhisperContext(long pointer) {
    super(pointer);
  }
}
