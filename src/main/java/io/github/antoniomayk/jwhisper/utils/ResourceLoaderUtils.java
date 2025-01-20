package io.github.antoniomayk.jwhisper.utils;

import java.util.MissingResourceException;
import org.jspecify.annotations.NonNull;

/**
 * Basic utility class to load resource files.
 *
 * @author Antonio Mayk
 * @since 0.1
 */
public class ResourceLoaderUtils {
  private ResourceLoaderUtils() {}

  /**
   * Get resource file from class loaders.
   *
   * @param clazz reference class
   * @param resourcePath the resource file location.
   * @return absolute file path.
   */
  @NonNull
  public static String getResource(
      @NonNull final Class<?> clazz, @NonNull final String resourcePath) {
    final var resourceUrl = clazz.getClassLoader().getResource(resourcePath);

    if (resourceUrl == null) {
      throw new MissingResourceException(
          "Resource not found '" + resourcePath + "'.", clazz.getName(), resourcePath);
    }

    return resourceUrl.getFile();
  }

  /**
   * Loads the native library specified by the libPath argument in the resources folder.
   *
   * @param clazz reference class to look for.
   * @param libPath library location.
   */
  public static void loadLibrary(@NonNull final Class<?> clazz, @NonNull final String libPath) {
    final var absolutePath = getResource(clazz, libPath);

    System.load(absolutePath);
  }
}
