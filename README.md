# NetGauge

[![Build and test](https://github.com/crazyuploader/netgauge/actions/workflows/build-and-test.yml/badge.svg?branch=main)](https://github.com/crazyuploader/netgauge/actions/workflows/build-and-test.yml)
[![Latest release](https://img.shields.io/github/v/release/crazyuploader/netgauge)](https://github.com/crazyuploader/netgauge/releases/latest)
[![License: Apache-2.0](https://img.shields.io/github/license/crazyuploader/netgauge)](LICENSE)

An Android app that opens an internet speed test website full screen, on phones, TVs and set-top boxes. It works with only a remote, no touch screen needed. Today it opens [FAST.com](https://fast.com/) with these settings already turned on:

| Setting | Value |
| --- | --- |
| Parallel connections | 8 to 16 |
| Test duration | 60 to 120 seconds |
| Measure loaded latency during upload | On |
| Always show all metrics | On |

The arrow keys on the remote are ignored, so the page does not scroll while a test runs. Back closes the app.

This project is not affiliated with or endorsed by Netflix. FAST.com is a Netflix service, and the app only opens the public website.

## Install

Download the APK from [Releases](https://github.com/crazyuploader/netgauge/releases) and install it:

```bash
adb install netgauge-<version>.apk
```

It runs on Android 7.0 or newer, on TVs, set-top boxes, and phones. It needs no Google services.

## Build

You need JDK 21 and the Android SDK, or only Docker:

```bash
./gradlew build                  # with a local JDK and SDK
tools/docker-build.sh build      # in Docker, nothing else installed
```

The debug APK is in `app/build/outputs/apk/debug/`. Before you send a change, run the same checks as CI:

```bash
./gradlew spotlessApply lintDebug testDebugUnitTest
```

## Add a speed test site

1. Add a file in `app/src/main/kotlin/com/devjugal/netgauge/provider/`, like `FastCom.kt`. Set the URL, the hosts the page may navigate to, an optional setup script, and the key policy.
2. Add it to `Providers.all` in `Providers.kt`.
3. Add a test in `app/src/test/`.

The app opens the last-used site. A site picker comes with the second site.

## Release

1. Set `versionCode` and `versionName` in `app/build.gradle.kts`, and add the changes to `CHANGELOG.md`.
2. Push a tag that matches `versionName`, for example `git tag v0.2.0 && git push origin v0.2.0`.

The "Publish signed release" workflow tests the code, builds the APK, signs it, checks the signing certificate, and attaches the APK and its SHA-256 file to a GitHub Release. It needs these repository secrets:

| Secret | Value |
| --- | --- |
| `SIGNING_KEYSTORE_BASE64` | The keystore file, base64 encoded |
| `SIGNING_STORE_PASSWORD` | The keystore password |
| `SIGNING_KEY_ALIAS` | The key alias |
| `SIGNING_KEY_PASSWORD` | The key password |

To sign a release build on your computer, create `keystore.properties` in the repository root. Git ignores it.

```properties
storeFile=/absolute/path/to/release.jks
storePassword=...
keyAlias=...
keyPassword=...
```

Without a key, `assembleRelease` builds an unsigned APK.

## License

[Apache License 2.0](LICENSE)
