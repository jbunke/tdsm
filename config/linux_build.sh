#!/bin/bash

ID="tdsm"
NAME="Top Down Sprite Maker"
DEV="Jordan Bunke"

echo "Building $NAME for Linux..."

ARTIFACT_DIR="../out/artifacts/release"
OUTPUT_DIR="../out/artifacts/dist/linux"

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

# Package application with jpackage for Linux
ICON_PATH="./icons/sources/icon-256px.png"
VERSION_FILE="../res/version"

VERSION=$(cat "$VERSION_FILE")
echo "Version: $VERSION"

jpackage \
    --type deb \
    --input "$ARTIFACT_DIR" \
    --dest "$OUTPUT_DIR/installer" \
    --name "$NAME" \
    --app-version "$VERSION" \
    --main-jar tdsm.jar \
    --runtime-image "$OUTPUT_DIR/runtime" \
    --icon "$ICON_PATH" \
    --linux-package-name "$ID" \
    --linux-deb-maintainer "$DEV <jordanbunkework@gmail.com>" \
    --linux-shortcut \
    --description "$NAME"
mv "$OUTPUT_DIR/installer/${ID}_${VERSION}-1_amd64.deb" \
   "$OUTPUT_DIR/installer/${ID}-${VERSION}-installer.deb"

if [[ $? -eq 0 ]]; then
  echo "3/4: Built Linux installer with jpackage"
else
  echo "Failed to build Linux installer"
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
  echo "4/4: Built Linux executable with jpackage"
else
  echo "Failed to build Linux executable"
fi
