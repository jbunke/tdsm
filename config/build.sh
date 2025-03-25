#!/bin/bash

NAME="Top Down Sprite Maker"
OUTPUT_DIR="../out/artifacts/release"
BUILD_DIR="../out/artifacts/dist"

# Analyze Java module dependencies
jdeps --multi-release 17 --print-module-deps "$OUTPUT_DIR/tdsm.jar" > "$OUTPUT_DIR/modules.txt"
echo "1/3: Analyzed dependencies"

MODULES=$(cat "$OUTPUT_DIR/modules.txt")
echo "Modules: $MODULES"

# Create custom runtime image with jlink
jlink --module-path "$JAVA_HOME/jmods" --add-modules "$MODULES" --output "$OUTPUT_DIR/runtime"
echo "2/3: Generated runtime image"

cmd.exe /c "$(wslpath -w "./win_build.bat")"

# Package application with jpackage for Linux
jpackage --input "$OUTPUT_DIR" --name "$NAME" --main-jar tdsm.jar --type deb --runtime-image "$OUTPUT_DIR/runtime" --dest "$BUILD_DIR/linux"

if [[ $? -eq 0 ]]; then
  echo "3/3c: Built $NAME for Linux"
else
  echo "Failed to build $NAME for Linux"
fi
