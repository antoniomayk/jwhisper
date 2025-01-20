package io.github.antoniomayk.jwhisper.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.MissingResourceException;
import org.junit.jupiter.api.Test;

class ResourceLoaderUtilsTest {
  @Test
  void shouldReturnNonEmptyPathForExistingResource() {
    final var filePath = "linux_amd64/.gitkeep";
    final var absolutePath =
        ResourceLoaderUtils.getResource(ResourceLoaderUtilsTest.class, filePath);

    assertThat(absolutePath).isNotEmpty();
  }

  @Test
  void shouldThrowExceptionWhenFileIsNotFound() {
    final var file = "file-not-found";

    assertThatThrownBy(() -> ResourceLoaderUtils.getResource(ResourceLoaderUtilsTest.class, file))
        .isInstanceOf(MissingResourceException.class)
        .hasMessage("Resource not found '" + file + "'.");
  }
}
