cmake_minimum_required(VERSION 3.10)

project(whisper-jni)

add_executable(${PROJECT_NAME} ${CMAKE_SOURCE_DIR}/src/main.c)
