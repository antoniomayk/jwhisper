package io.github.antoniomayk.jwhisper.utils;

import java.io.File;
import java.util.Locale;
import org.jspecify.annotations.Nullable;

/**
 * Utility class for library loading.
 *
 * @author Antonio Mayk
 * @since 0.1
 */
public class LibraryUtils {

  private LibraryUtils() {
    // Private constructor to prevent instantiation
  }

  /**
   * Generates the resource path for a native library based on the current operating system and
   * architecture.
   *
   * <p>This method determines the appropriate file extension and prefix for the library based on
   * the operating system, and constructs a path that includes the OS and architecture information.
   *
   * @param libName The name of the library without any prefix or extension.
   * @return A String representing the resource path for the library, including the OS-specific
   *     directory, prefix (if applicable), library name, and appropriate file extension.
   * @throws UnsupportedOperationException If the current operating system is not supported.
   */
  public static String getLibraryResourcePath(@Nullable String libName) {
    if (libName == null || libName.isEmpty()) {
      throw new IllegalArgumentException("Library name cannot be null or empty");
    }

    final var os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
    final var arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);

    String libExtension;
    String libPrefix;

    if (os.contains("win")) {
      libExtension = ".dll";
      libPrefix = "";
    } else if (os.contains("mac")) {
      libExtension = ".dylib";
      libPrefix = "lib";
    } else if (os.contains("nix") || os.contains("nux")) {
      libExtension = ".so";
      libPrefix = "lib";
    } else {
      throw new UnsupportedOperationException("Unsupported OS: " + os);
    }

    return String.join(File.separator, os + "_" + arch, libPrefix + libName + libExtension);
  }
}
