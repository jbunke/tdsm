#!/bin/bash

ID="tdsm"
NAME="Top Down Sprite Maker"
DEV="Jordan Bunke"
MAC_BUNDLE_ID="com.jordanbunke.tdsm"

echo "Building $NAME for macOS..."

ARTIFACT_DIR="../out/artifacts/release"
OUTPUT_DIR="../out/artifacts/dist/mac"

rm -rf "$OUTPUT_DIR/build"
rm -rf "$OUTPUT_DIR/runtime"
mkdir -p "$OUTPUT_DIR"

# Analyze Java module dependencies
jdeps --multi-release 17 --print-module-deps "$ARTIFACT_DIR/tdsm.jar" > "$OUTPUT_DIR/modules.txt"
echo "1/4: Analyzed dependencies"

MODULES=$(cat "$OUTPUT_DIR/modules.txt")
echo "Modules: $MODULES"

# Create custom runtime image with jlink
jlink --module-path "$JAVA_HOME/jmods" --add-modules "$MODULES" --output "$OUTPUT_DIR/runtime"
echo "2/4: Generated runtime image"

# Package application with jpackage for macOS
ICON_PATH="./icons/mac-icon.icns"
VERSION_FILE="../res/version"

VERSION=$(cat "$VERSION_FILE")
echo "Version: $VERSION"

jpackage \
    --type dmg \
    --input "$ARTIFACT_DIR" \
    --dest "$OUTPUT_DIR/installer" \
    --name "$NAME" \
    --app-version "$VERSION" \
    --main-jar tdsm.jar \
    --runtime-image "$OUTPUT_DIR/runtime" \
    --icon "$ICON_PATH" \
    --vendor "$DEV" \
    --mac-package-identifier "$MAC_BUNDLE_ID" \
    --description "$NAME"
mv "$OUTPUT_DIR/installer/${NAME}-${VERSION}.dmg" \
   "$OUTPUT_DIR/installer/${ID}-${VERSION}-installer.dmg"

if [[ $? -eq 0 ]]; then
  echo "3/4: Built macOS installer with jpackage"
else
  echo "Failed to build macOS installer"
fi

jpackage \
    --type app-image \
    --input "$ARTIFACT_DIR" \
    --dest "$OUTPUT_DIR/build" \
    --name "$ID" \
    --app-version "$VERSION" \
    --main-jar tdsm.jar \
    --runtime-image "$OUTPUT_DIR/runtime" \
    --icon "$ICON_PATH" \
    --vendor "$DEV" \
    --description "$NAME"

if [[ $? -eq 0 ]]; then
  echo "4/4: Built macOS executable with jpackage"
else
  echo "Failed to build macOS executable"
fi
