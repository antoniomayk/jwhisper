package io.github.antoniomayk.jwhisper.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.junit.jupiter.api.Test;

class LibraryUtilsTest {
  @Test
  void shouldReturnCorrectPathForWindowsWithX86Architecture() {
    final var libName = "testlib";

    System.setProperty("os.name", "Windows 10");
    System.setProperty("os.arch", "x86");

    final var result = LibraryUtils.getLibraryResourcePath(libName);

    assertEquals("windows 10_x86" + File.separator + "testlib.dll", result);
  }

  @Test
  void shouldReturnCorrectPathForMacWithArmArchitecture() {
    final var libName = "testlib";

    System.setProperty("os.name", "Mac OS X");
    System.setProperty("os.arch", "aarch64");

    final var result = LibraryUtils.getLibraryResourcePath(libName);

    assertEquals("mac os x_aarch64" + File.separator + "libtestlib.dylib", result);
  }

  @Test
  void shouldReturnCorrectPathForLinuxWithX64Architecture() {
    final var libName = "testlib";

    System.setProperty("os.name", "Linux");
    System.setProperty("os.arch", "amd64");

    final var result = LibraryUtils.getLibraryResourcePath(libName);

    assertEquals("linux_amd64" + File.separator + "libtestlib.so", result);
  }

  @Test
  void shouldReturnCorrectPathForUnixWithX64Architecture() {
    final var libName = "testlib";

    System.setProperty("os.name", "Unix");
    System.setProperty("os.arch", "amd64");

    final var result = LibraryUtils.getLibraryResourcePath(libName);

    assertEquals("unix_amd64" + File.separator + "libtestlib.so", result);
  }

  @Test
  void shouldThrowUnsupportedOperationExceptionForUnsupportedOs() {
    final var libName = "testlib";

    System.setProperty("os.name", "Unsupported OS");
    System.setProperty("os.arch", "x86");

    assertThrows(
        UnsupportedOperationException.class,
        () -> {
          LibraryUtils.getLibraryResourcePath(libName);
        },
        "UnsupportedOperationException should be thrown for unsupported OS");
  }

  @Test
  void shouldCorrectlyAppendLibPrefixForMacAndLinux() {
    final var libName = "testlib";

    System.setProperty("os.name", "Mac OS X");
    System.setProperty("os.arch", "x86_64");

    final var macResult = LibraryUtils.getLibraryResourcePath(libName);
    assertTrue(macResult.contains("libtestlib.dylib"));

    System.setProperty("os.name", "Linux");
    System.setProperty("os.arch", "amd64");

    final var linuxResult = LibraryUtils.getLibraryResourcePath(libName);
    assertTrue(linuxResult.contains("libtestlib.so"));

    System.setProperty("os.name", "Windows 10");
    System.setProperty("os.arch", "amd64");

    final var windowsResult = LibraryUtils.getLibraryResourcePath(libName);
    assertFalse(windowsResult.contains("libtestlib"));
    assertTrue(windowsResult.contains("testlib.dll"));
  }

  @Test
  void shouldUseCorrectFileExtensionBasedOnOs() {
    final var libName = "testlib";

    System.setProperty("os.name", "Windows 10");
    System.setProperty("os.arch", "x64");

    final var windowsResult = LibraryUtils.getLibraryResourcePath(libName);
    assertTrue(windowsResult.endsWith(".dll"));

    System.setProperty("os.name", "Mac OS X");
    System.setProperty("os.arch", "x86_64");

    final var macResult = LibraryUtils.getLibraryResourcePath(libName);
    assertTrue(macResult.endsWith(".dylib"));

    System.setProperty("os.name", "Linux");
    System.setProperty("os.arch", "amd64");

    final var linuxResult = LibraryUtils.getLibraryResourcePath(libName);
    assertTrue(linuxResult.endsWith(".so"));
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenLibraryNameIsEmpty() {
    assertThrows(
        IllegalArgumentException.class,
        () -> LibraryUtils.getLibraryResourcePath(""),
        "IllegalArgumentException should be thrown for empty library name");
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenLibraryNameIsNull() {
    assertThrows(
        IllegalArgumentException.class,
        () -> LibraryUtils.getLibraryResourcePath(null),
        "IllegalArgumentException should be thrown when library name is null");
  }
}
