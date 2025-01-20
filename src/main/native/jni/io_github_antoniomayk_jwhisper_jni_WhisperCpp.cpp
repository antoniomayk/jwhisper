#include "jni.h"
#include <whisper.h>

#include <io_github_antoniomayk_jwhisper_jni_WhisperCpp.h>

#include <iostream>
#include <unordered_map>

namespace util {

template <typename T>
T *cast_j_cpointer_to_pointer(JNIEnv *env, jobject j_cpointer) {
  auto j_cls = env->GetObjectClass(j_cpointer);
  auto j_fid = env->GetFieldID(j_cls, "pointer", "J");
  return (T *)env->GetLongField(j_cpointer, j_fid);
}

template <typename T>
T cast_j_cpointer_to_value(JNIEnv *env, jobject j_cpointer) {
  auto j_cls = env->GetObjectClass(j_cpointer);
  auto j_fid = env->GetFieldID(j_cls, "pointer", "J");
  return *(T *)env->GetLongField(j_cpointer, j_fid);
}

std::string get_whisper_full_error_message(int errorCode) {
  static const std::unordered_map<int, std::string> errorMessages = {
      {-2, "Failed to compute log mel spectrogram"},
      {-3, "Failed to auto-detect language"},
      {-4, "Too many decoders requested"},
      {-5, "Audio context is larger than maximum allowed"},
      {-6, "Failed to encode"},
      {-7, "Failed for self-attention cache"},
      {-8, "Failed to decode"},
      {-9, "Failed to decode"}};

  auto it = errorMessages.find(errorCode);
  if (it != errorMessages.end()) {
    return it->second;
  }

  return "Unknown error";
}

} // namespace util

JNIEXPORT jobject JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperInitFromFileWithParams(
    JNIEnv *env, jclass, jstring j_path_model, jobject j_params) {
  auto path_model = env->GetStringUTFChars(j_path_model, nullptr);
  auto params = util::cast_j_cpointer_to_value<struct whisper_context_params>(
      env, j_params);

  auto j_cls =
      env->FindClass("Lio/github/antoniomayk/jwhisper/jni/WhisperContext;");
  auto j_mid = env->GetMethodID(j_cls, "<init>", "(J)V");
  auto j_obj = env->NewObject(
      j_cls, j_mid,
      (jlong)whisper_init_from_file_with_params(path_model, params));

  env->ReleaseStringUTFChars(j_path_model, path_model);

  return j_obj;
}

JNIEXPORT void JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperFree(JNIEnv *env,
                                                               jclass,
                                                               jobject j_ctx) {
  whisper_free(
      util::cast_j_cpointer_to_pointer<struct whisper_context>(env, j_ctx));
}

JNIEXPORT void JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperFreeParams(
    JNIEnv *env, jclass, jobject j_params) {
  whisper_free_params(
      util::cast_j_cpointer_to_pointer<struct whisper_full_params>(env,
                                                                   j_params));
}

JNIEXPORT void JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperFreeContextParams(
    JNIEnv *env, jclass, jobject j_params) {
  whisper_free_context_params(
      util::cast_j_cpointer_to_pointer<struct whisper_context_params>(
          env, j_params));
}

JNIEXPORT jobject JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperContextDefaultParamsByRef(
    JNIEnv *env, jclass) {
  auto j_cls = env->FindClass(
      "Lio/github/antoniomayk/jwhisper/jni/WhisperContextParams;");
  auto j_mid = env->GetMethodID(j_cls, "<init>", "(J)V");
  return env->NewObject(j_cls, j_mid,
                        (jlong)whisper_context_default_params_by_ref());
}

JNIEXPORT jobject JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperFullDefaultParamsByRef(
    JNIEnv *env, jclass, jint j_strategy) {
  auto strategy = (enum whisper_sampling_strategy)j_strategy;
  auto j_cls =
      env->FindClass("Lio/github/antoniomayk/jwhisper/jni/WhisperFullParams;");
  auto j_mid = env->GetMethodID(j_cls, "<init>", "(J)V");
  return env->NewObject(j_cls, j_mid,
                        (jlong)whisper_full_default_params_by_ref(strategy));
}

JNIEXPORT void JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperFull(
    JNIEnv *env, jclass, jobject j_ctx, jobject j_params, jfloatArray j_samples,
    jint j_n_samples) {
  auto ctx =
      util::cast_j_cpointer_to_pointer<struct whisper_context>(env, j_ctx);
  auto params =
      util::cast_j_cpointer_to_value<struct whisper_full_params>(env, j_params);
  auto samples = env->GetFloatArrayElements(j_samples, nullptr);
  auto n_samples = j_n_samples;
  auto result = whisper_full(ctx, params, samples, n_samples);

  if (result < 0) {
    std::string errorMessage = util::get_whisper_full_error_message(result);
    env->ThrowNew(
        env->FindClass(
            "Lio/github/antoniomayk/jwhisper/exceptions/WhisperFullException;"),
        errorMessage.c_str());
  }

  env->ReleaseFloatArrayElements(j_samples, samples, 0);
}

JNIEXPORT jint JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperFullNumberOfSegments(
    JNIEnv *env, jclass, jobject j_ctx) {
  auto j_cls_whisper_context = env->GetObjectClass(j_ctx);
  auto j_fid_whisper_context_pointer =
      env->GetFieldID(j_cls_whisper_context, "pointer", "J");
  auto ctx = (struct whisper_context *)env->GetLongField(
      j_ctx, j_fid_whisper_context_pointer);

  return whisper_full_n_segments(ctx);
}

JNIEXPORT jstring JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperFullGetSegmentText(
    JNIEnv *env, jclass, jobject j_ctx, jint j_i_segment) {
  auto ctx =
      util::cast_j_cpointer_to_pointer<struct whisper_context>(env, j_ctx);
  auto i_segment = j_i_segment;

  auto segment_text = whisper_full_get_segment_text(ctx, i_segment);

  return env->NewStringUTF(segment_text);
}

JNIEXPORT jlong JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperFullGetSegmentT0(
    JNIEnv *env, jclass, jobject j_ctx, jint j_i_segment) {
  auto ctx =
      util::cast_j_cpointer_to_pointer<struct whisper_context>(env, j_ctx);
  auto i_segment = j_i_segment;

  return whisper_full_get_segment_t0(ctx, i_segment);
}

JNIEXPORT jlong JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperFullGetSegmentT1(
    JNIEnv *env, jclass, jobject j_ctx, jint j_i_segment) {
  auto ctx =
      util::cast_j_cpointer_to_pointer<struct whisper_context>(env, j_ctx);
  auto i_segment = j_i_segment;

  return whisper_full_get_segment_t1(ctx, i_segment);
}
