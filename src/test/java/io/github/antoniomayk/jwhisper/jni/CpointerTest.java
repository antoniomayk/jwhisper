package io.github.antoniomayk.jwhisper.jni;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CpointerTest {
  @Test
  void testGetPointer() {
    final var expectedPointer = 12345L;
    final var cpointer = new Cpointer(expectedPointer);
    final var result = cpointer.getPointer();

    assertEquals(expectedPointer, result);
  }
}
