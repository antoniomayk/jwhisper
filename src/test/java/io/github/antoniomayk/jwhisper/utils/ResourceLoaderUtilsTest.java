package io.github.antoniomayk.jwhisper.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.MissingResourceException;
import org.junit.jupiter.api.Test;

/** Checks integrity of {@link ResourceLoaderUtils} methods. */
final class ResourceLoaderUtilsTest {

  private ResourceLoaderUtilsTest() {}

  /** Asserts that should return a nonempty string when file is found in the resources folder. */
  @Test
  void assertAbsoluteFilePath() {
    final var filePath = "linux-x86_64/.gitkeep";
    final var absolutePath =
        ResourceLoaderUtils.getResource(ResourceLoaderUtilsTest.class, filePath);

    assertThat(absolutePath).isNotEmpty();
  }

  /** Asserts that when file is not found should throw exception. */
  @Test
  void assertThrowWhenFileIsNotFound() {
    final var file = "file-not-found";

    assertThatThrownBy(() -> ResourceLoaderUtils.getResource(ResourceLoaderUtilsTest.class, file))
        .isInstanceOf(MissingResourceException.class)
        .hasMessage("Resource not found '" + file + "'.");
  }
}
