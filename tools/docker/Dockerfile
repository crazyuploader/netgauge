# Android build environment for computers without a JDK or Android SDK.
# Used by tools/docker-build.sh. CI does not use it (GitHub runners have the SDK).
FROM eclipse-temurin:21-jdk

ARG CMDLINE_TOOLS_VERSION=16111833
ARG CMDLINE_TOOLS_SHA1=e025545c62a8e64c7559119566a569fb1dec5f60
# Match compileSdk and the build-tools version the Android Gradle plugin uses by default.
ARG COMPILE_SDK=37.0
ARG BUILD_TOOLS=36.0.0

ENV ANDROID_HOME=/opt/android-sdk \
    GRADLE_USER_HOME=/gradle \
    DEBIAN_FRONTEND=noninteractive

RUN apt-get update \
    && apt-get install -y --no-install-recommends unzip \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p "$ANDROID_HOME/cmdline-tools" \
    && curl -fsSL -o /tmp/tools.zip \
       "https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS_VERSION}_latest.zip" \
    && echo "${CMDLINE_TOOLS_SHA1}  /tmp/tools.zip" | sha1sum -c - \
    && unzip -q /tmp/tools.zip -d "$ANDROID_HOME/cmdline-tools" \
    && mv "$ANDROID_HOME/cmdline-tools/cmdline-tools" "$ANDROID_HOME/cmdline-tools/latest" \
    && rm /tmp/tools.zip

ENV PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/build-tools/${BUILD_TOOLS}:$PATH"

RUN yes | sdkmanager --licenses > /dev/null \
    && sdkmanager --install "platforms;android-${COMPILE_SDK}" "build-tools;${BUILD_TOOLS}" \
    && chmod -R a+rX "$ANDROID_HOME" \
    && mkdir -p /gradle && chmod 777 /gradle

ENV HOME=/gradle
WORKDIR /repo
