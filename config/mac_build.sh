#!/bin/bash

NAME="Top Down Sprite Maker"

echo "Building $NAME for macOS..."

ARTIFACT_DIR="../out/artifacts/release"
OUTPUT_DIR="../out/artifacts/dist/mac"

mkdir -p "$OUTPUT_DIR"

# Analyze Java module dependencies
jdeps --multi-release 17 --print-module-deps "$ARTIFACT_DIR/tdsm.jar" > "$OUTPUT_DIR/modules.txt"
echo "1/3: Analyzed dependencies"

MODULES=$(cat "$OUTPUT_DIR/modules.txt")
echo "Modules: $MODULES"

# Create custom runtime image with jlink
jlink --module-path "$JAVA_HOME/jmods" --add-modules "$MODULES" --output "$OUTPUT_DIR/runtime"
echo "2/3: Generated runtime image"

# Package application with jpackage for macOS
ICON_PATH="../out/artifacts/_to bundle with releases/icons/sources/icon-256px.png"
VERSION_FILE="../res/version"

VERSION=$(cat "$VERSION_FILE")
echo "Version: $VERSION"

jpackage \
    --type dmg \
    --input "$ARTIFACT_DIR" \
    --dest "$OUTPUT_DIR/build" \
    --name "$NAME" \
    --app-version "$VERSION" \
    --main-jar tdsm.jar \
    --runtime-image "$OUTPUT_DIR/runtime" \
    --icon "$ICON_PATH" \
    --mac-package-identifier "tdsm" \
    --description "$NAME is a tool for customizing and exporting pixel art sprites."

if [[ $? -eq 0 ]]; then
  echo "3/3: Built $NAME for macOS"
else
  echo "Failed to build $NAME for macOS"
fi
