cmake_minimum_required(VERSION 3.10)

set(CMAKE_C_STANDARD 23)
set(CMAKE_C_COMPILER clang)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

project(jwhisper C CXX)

add_subdirectory(${CMAKE_SOURCE_DIR}/src/main/native/external/whisper.cpp)

add_library(
  ${PROJECT_NAME} SHARED
  ${CMAKE_SOURCE_DIR}/src/main/native/jni/io_github_antoniomayk_jwhisper_jni_WhisperCpp.c
)
target_include_directories(
  ${PROJECT_NAME}
  PRIVATE ${CMAKE_SOURCE_DIR}/include ${CMAKE_SOURCE_DIR}/src/main/native/jni
          $ENV{JAVA_HOME}/include $ENV{JAVA_HOME}/include/linux
          ${CMAKE_SOURCE_DIR}/src/main/native/external/whisper.cpp/spm-headers)
target_link_libraries(${PROJECT_NAME} PRIVATE whisper)
