package io.github.antoniomayk.jwhisper.jni;

/**
 * Represents a C pointer.
 *
 * @author Antonio Mayk
 * @since 0.1
 */
public class Cpointer {
  private final long pointer;

  public Cpointer(long pointer) {
    this.pointer = pointer;
  }

  public long getPointer() {
    return pointer;
  }
}
