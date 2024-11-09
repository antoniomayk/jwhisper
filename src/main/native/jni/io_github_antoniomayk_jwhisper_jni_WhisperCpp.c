#include <whisper.h>

#include <io_github_antoniomayk_jwhisper_jni_WhisperCpp.h>

JNIEXPORT jlong JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperInitFromFileWithParams(
    JNIEnv *env, jclass, jstring j_path_model, jlong j_params) {
  const char *path_model;
  struct whisper_context_params params;
  struct whisper_context *ctx;

  path_model = (*env)->GetStringUTFChars(env, j_path_model, NULL);
  params = *(struct whisper_context_params *)j_params;
  ctx = whisper_init_from_file_with_params(path_model, params);

  (*env)->ReleaseStringUTFChars(env, j_path_model, path_model);

  return (jlong)ctx;
}

JNIEXPORT void JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperFree(JNIEnv *, jclass,
                                                               jlong j_ctx) {
  struct whisper_context *ctx = (struct whisper_context *)j_ctx;

  whisper_free(ctx);
}

JNIEXPORT void JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperFreeParams(
    JNIEnv *, jclass, jlong j_params) {
  struct whisper_full_params *params = (struct whisper_full_params *)j_params;

  whisper_free_params(params);
}

JNIEXPORT void JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperFreeContextParams(
    JNIEnv *, jclass, jlong j_params) {
  struct whisper_context_params *params =
      (struct whisper_context_params *)j_params;

  whisper_free_context_params(params);
}

JNIEXPORT jlong JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperContextDefaultParamsByRef(
    JNIEnv *, jclass) {
  return (jlong)whisper_context_default_params_by_ref();
}

JNIEXPORT jlong JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperFullDefaultParamsByRef(
    JNIEnv *, jclass, jint j_strategy) {
  enum whisper_sampling_strategy strategy =
      (enum whisper_sampling_strategy)j_strategy;

  return (jlong)whisper_full_default_params_by_ref(strategy);
}

JNIEXPORT jint JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperFull(
    JNIEnv *env, jclass, jlong j_ctx, jlong j_params, jfloatArray j_samples,
    jint j_n_samples) {
  struct whisper_context *ctx;
  struct whisper_full_params params;
  float *samples;
  int n_samples;
  int result;

  ctx = (struct whisper_context *)j_ctx;
  params = *(struct whisper_full_params *)j_params;
  samples = (*env)->GetFloatArrayElements(env, j_samples, NULL);
  n_samples = (int)j_n_samples;

  result = whisper_full(ctx, params, samples, n_samples);

  (*env)->ReleaseFloatArrayElements(env, j_samples, samples, 0);

  return result;
}

JNIEXPORT jint JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperFullNumberOfSegments(
    JNIEnv *, jclass, jlong j_ctx) {
  return whisper_full_n_segments((struct whisper_context *)j_ctx);
}

JNIEXPORT jstring JNICALL
Java_io_github_antoniomayk_jwhisper_jni_WhisperCpp_whisperFullGetSegmentText(
    JNIEnv *env, jclass, jlong j_ctx, jint j_i_segment) {
  struct whisper_context *ctx;
  int i_segment;
  const char *segment_text;

  ctx = (struct whisper_context *)j_ctx;
  i_segment = (int)j_i_segment;

  segment_text = whisper_full_get_segment_text(ctx, i_segment);

  return (*env)->NewStringUTF(env, segment_text);
}
