#!/bin/bash

ID="tdsm"
NAME="Top Down Sprite Maker"
DEV="Jordan Bunke"

echo "Building $NAME for Linux..."

ARTIFACT_DIR="../out/artifacts/release"
OUTPUT_DIR="../out/bin/linux"

rm -rf "$OUTPUT_DIR/build"
rm -rf "$OUTPUT_DIR/runtime"
mkdir -p "$OUTPUT_DIR"

# Analyze Java module dependencies
jdeps --multi-release 17 --print-module-deps "$ARTIFACT_DIR/$ID.jar" > "$OUTPUT_DIR/modules.txt"
echo "1/4: Analyzed dependencies"

MODULES=$(cat "$OUTPUT_DIR/modules.txt")
echo "Modules: $MODULES"

# Create custom runtime image with jlink
jlink --module-path "$JAVA_HOME/jmods" --add-modules "$MODULES" --output "$OUTPUT_DIR/runtime"
echo "2/4: Generated runtime image"

# Package application with jpackage for Linux
ICON_PATH="./icons/sources/icon-256px.png"
VERSION_FILE="../res/version"
RELEASE_FILE="../res/release"
MARKETPLACE_FILE="../res/marketplace"

VERSION=$(cat "$VERSION_FILE")
echo "Version: $VERSION"

RELEASE_MODE=$(cat "$RELEASE_FILE")
echo "Release mode: $RELEASE_MODE"

MARKETPLACE=$(cat "$MARKETPLACE_FILE")
echo "Marketplace: $MARKETPLACE"

FILENAME_BASIS="${ID}-${VERSION}"
if [[ $RELEASE_MODE == "demo" ]]; then FILENAME_BASIS+="-demo"; fi
if [[ $MARKETPLACE != "itch.io" ]]; then FILENAME_BASIS+="-${MARKETPLACE}"; fi

if [[ $MARKETPLACE == "itch.io" && $RELEASE_MODE == "release" ]]; then
  jpackage \
      --type deb \
      --input "$ARTIFACT_DIR" \
      --dest "$OUTPUT_DIR/installer" \
      --name "$NAME" \
      --app-version "$VERSION" \
      --main-jar "$ID.jar" \
      --runtime-image "$OUTPUT_DIR/runtime" \
      --icon "$ICON_PATH" \
      --linux-package-name "$ID" \
      --linux-deb-maintainer "$DEV <jordanbunkework@gmail.com>" \
      --linux-shortcut \
      --description "$NAME"
  mv "$OUTPUT_DIR/installer/${ID}_${VERSION}-1_amd64.deb" \
     "$OUTPUT_DIR/installer/${FILENAME_BASIS}-installer.deb"

  if [[ $? -eq 0 ]]; then
    echo "3/4: Built Linux installer with jpackage"
  else
    echo "Failed to build Linux installer"
  fi
fi

jpackage \
    --type app-image \
    --input "$ARTIFACT_DIR" \
    --dest "$OUTPUT_DIR/build" \
    --name "$ID" \
    --app-version "$VERSION" \
    --main-jar "$ID.jar" \
    --runtime-image "$OUTPUT_DIR/runtime" \
    --icon "$ICON_PATH" \
    --vendor "$DEV" \
    --description "$NAME"
mv "$OUTPUT_DIR/build/$ID" "$OUTPUT_DIR/${FILENAME_BASIS}-linux"

if [[ $? -eq 0 ]]; then
  echo "4/4: Built Linux executable with jpackage"
else
  echo "Failed to build Linux executable"
fi
