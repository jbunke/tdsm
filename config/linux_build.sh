#!/bin/bash

NAME="Top Down Sprite Maker"

echo "Building $NAME for Linux..."

ARTIFACT_DIR="../out/artifacts/release"
OUTPUT_DIR="../out/artifacts/dist/linux"
VERSION_FILE="../res/version"

mkdir -p "$OUTPUT_DIR"

# Analyze Java module dependencies
jdeps --multi-release 17 --print-module-deps "$ARTIFACT_DIR/tdsm.jar" > "$OUTPUT_DIR/modules.txt"
echo "1/3: Analyzed dependencies"

MODULES=$(cat "$OUTPUT_DIR/modules.txt")
echo "Modules: $MODULES"

# Create custom runtime image with jlink
jlink --module-path "$JAVA_HOME/jmods" --add-modules "$MODULES" --output "$OUTPUT_DIR/runtime"
echo "2/3: Generated runtime image"

# Package application with jpackage for Linux
# jpackage --input "$ARTIFACT_DIR" --name "$NAME" --main-jar tdsm.jar --type deb --runtime-image "$OUTPUT_DIR/runtime" --dest "$OUTPUT_DIR/build"
VERSION=$(cat "$VERSION_FILE")
echo "Version: $VERSION"

TMP_DIR="/tmp/jpackage_temp"
mkdir -p "$TMP_DIR"

jpackage \
    --type deb \
    --input "$ARTIFACT_DIR" \
    --dest "$OUTPUT_DIR/build" \
    --name "$NAME" \
    --app-version "$VERSION" \
    --main-jar tdsm.jar \
    --runtime-image "$OUTPUT_DIR/runtime" \
    --linux-package-name "tdsm" \
    --temp "$TMP_DIR" \
    --linux-deb-maintainer "Jordan Bunke <schlankundflink@gmail.com>" \
    --description "$NAME is a tool for customizing and exporting pixel art sprites." \
    --verbose

if [[ $? -eq 0 ]]; then
  echo "3/3: Built $NAME for Linux"
else
  echo "Failed to build $NAME for Linux"
fi

# Clean up temporary directory
rm -rf "$TMP_DIR"
