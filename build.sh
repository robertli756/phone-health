#!/usr/bin/env bash
# Build Phone Health / 手机健康 v1.0 — Chinese and English APKs.
# Manual toolchain (no Gradle daemon needed):
#   javac -> d8 -> aapt2 link -> zipalign -> apksigner
# Usage: bash build.sh
# Outputs: apk/phone-health-zh-v1.0.apk, apk/phone-health-en-v1.0.apk
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
JAVA_HOME="$HOME/workspace/jdk-17.0.10+7"
BT="$HOME/workspace/android-sdk/build-tools/34.0.0"
ANDROID_JAR="$HOME/workspace/android-sdk/platforms/android-34/android.jar"
KEYSTORE="$ROOT/build/debug.keystore"
SRC="$ROOT/app/src/main/java"
OUT="$ROOT/apk"

export JAVA_HOME
export PATH="$JAVA_HOME/bin:$PATH"

build_one() {
    local lang="$1"      # zh | en
    local manifest="$2"  # manifest file to use
    local apk="$3"       # output apk name
    local work="$ROOT/build/variant-$lang"
    rm -rf "$work"
    mkdir -p "$work/classes" "$work/dex" "$OUT"

    # Copy sources; flip the language flag for the English build.
    cp -r "$SRC" "$work/src"
    if [ "$lang" = "en" ]; then
        sed -i 's/public static final boolean EN = false;/public static final boolean EN = true;/' \
            "$work/src/com/robert/phonehealth/Strings.java"
    fi

    # 1. Compile Java (UTF-8 source). android.jar goes on -classpath
    # (javac 17 forbids -bootclasspath with -target 17).
    javac -encoding UTF-8 -source 17 -target 17 \
        -classpath "$ANDROID_JAR" \
        -d "$work/classes" \
        "$work/src/com/robert/phonehealth/"*.java

    # 2. Dex.
    "$BT/d8" --lib "$ANDROID_JAR" --output "$work/dex" \
        "$work/classes/com/robert/phonehealth/"*.class

    # 3. Link manifest into an APK, then add classes.dex.
    "$BT/aapt2" link -o "$work/unsigned.apk" --manifest "$manifest" -I "$ANDROID_JAR"
    (cd "$work/dex" && zip -q "$work/unsigned.apk" classes.dex)

    # 4. Align + sign (same debug key as v1.0, so updates install over it).
    "$BT/zipalign" -f 4 "$work/unsigned.apk" "$work/aligned.apk"
    "$BT/apksigner" sign --ks "$KEYSTORE" --ks-pass pass:android \
        --ks-key-alias androiddebugkey --key-pass pass:android \
        --out "$OUT/$apk" "$work/aligned.apk"

    # 5. Verify.
    "$BT/apksigner" verify --verbose "$OUT/$apk" | tail -2
    "$BT/aapt2" dump badging "$OUT/$apk" | grep -E "package:|application-label"
}

build_one zh "$ROOT/app/src/main/AndroidManifest.xml"    phone-health-zh-v1.0.apk
build_one en "$ROOT/app/src/main/AndroidManifest-en.xml" phone-health-en-v1.0.apk

ls -la "$OUT"
echo "DONE: both APKs built and signed."
