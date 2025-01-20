# Jwhisper - Java Whisper Binding

Jwhisper is a lightweight Java API for interacting with the [whisper.cpp](https://github.com/ggerganov/whisper.cpp) project, providing easy access to Whisper's speech recognition capabilities in Java applications.

## ⚠️ Warning

The Java API is currently in development and may undergo changes. For production use, consider the following stable alternatives:

- [whisper-jni](https://github.com/GiviMAD/whisper-jni)
- [whisper.cpp Java bindings](https://github.com/ggerganov/whisper.cpp/blob/master/bindings/java/README.md)

## 🛠️ Building the Project

### Maven Profiles

The project uses the following Maven profiles:

- **JNI**: Generates JNI header files
- **CMAKE**: Builds the `whisper.cpp` library and `libjwhisper.so`
- **LINUX_AMD64**: Copies `libjwhisper.so` to the resource folder

### Generate Package

To build the project, run the following commands:

```bash
mvn clean
mvn compile -P jni
mvn compile -P cmake,linux_amd64
```

## 🚀 Quick Start

Here's a simple example of how to use Jwhisper:

```java
import java.nio.file.Paths;
import javax.sound.sampled.AudioSystem;
import java.io.File;

public class JwhisperExample {
    public static void main(String[] args) {
        try (Jwhisper jwhisper = Jwhisper.newInstance(Paths.get(System.getenv("GGML_MODELS"), "ggml-tiny.bin"))) {
            // Load audio file
            String wavFile = ResourceLoaderUtils.getResource(JwhisperTest.class, "samples/jfk.wav");
            var wavInputStream = AudioSystem.getAudioInputStream(new File(wavFile));
            float[] samples = AudioUtils.convertPcmS16MonoToPcmF32Mono(wavInputStream);

            // Perform speech recognition
            int segmentsSize = jwhisper.whisperFull(samples);

            // Get and print the recognized text
            String recognizedText = jwhisper.whisperFullGetSegmentText(0).trim();
            System.out.println("Recognized text: " + recognizedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## 📋 Prerequisites

* Java Development Kit (JDK) 11 or later
* Maven
* CMake (for building native libraries)
* A C++ compiler compatible with C++11

### Environment Variables

The following environment variables need to be set:

```bash
export JAVA_HOME=$(readlink -f /usr/bin/javac | sed "s:/bin/javac::")
export GGML_MODELS="path/to/the/ggml/model.bin"

# For SonarQube validation (optional):

export SONAR_PROJECT_KEY="project_key"
export SONAR_PROJECT_NAME="project_name"
export SONAR_HOST_URL="host_url"
export SONAR_TOKEN="token"
```

## 📄 License

Distributed under the MIT License. See LICENSE file for more information.

## 🙏 Acknowledgments

Special thanks to the whisper.cpp contributors for their excellent work.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (git checkout -b feature/AmazingFeature)
3. Commit your changes (git commit -m 'Add some AmazingFeature')
4. Push to the branch (git push origin feature/AmazingFeature)
5. Open a Pull Request

## 📬 Contact

If you have any questions or suggestions, please open an issue in this repository.
