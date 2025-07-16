@echo off

setlocal

set "NAME=Top Down Sprite Maker"

echo Building %NAME% for Windows...

set "ARTIFACT_DIR=..\out\artifacts\release"
set "OUTPUT_DIR=..\out\artifacts\dist\win"

if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

:: Analyze Java module dependencies
jdeps --multi-release 17 --print-module-deps "%ARTIFACT_DIR%\tdsm.jar" > "%OUTPUT_DIR%\modules.txt"
echo 1/3: Analyzed dependencies

:: Read modules from file
set /p MODULES=<"%OUTPUT_DIR%\modules.txt"
echo Modules: %MODULES%

:: Generate runtime image
set JAVA_MOD_PATH="C:\Program Files\Java\jdk-17\jmods"

jlink --module-path %JAVA_MOD_PATH% --add-modules "%MODULES%" --output "%OUTPUT_DIR%\runtime"
echo 2/3: Generated runtime image

:: Build the EXE with Launch4J
:: call l4j.bat
:: echo 3/4: Built Windows executable with Launch4J

:: Compile installer script
:: iscc installer_script.iss
:: echo 4/4: Compiled Windows installer script

set "VERSION_FILE=..\res\version"
set /p VERSION=<"%VERSION_FILE%"

set ICON_PATH="..\out\artifacts\bundle\icons\win-icon.ico"

jpackage ^
    --type exe ^
    --input "%ARTIFACT_DIR%" ^
    --dest "%OUTPUT_DIR%\build" ^
    --name "%NAME%" ^
    --app-version "%VERSION%" ^
    --main-jar tdsm.jar ^
    --runtime-image "%OUTPUT_DIR%\runtime" ^
    --icon "%ICON_PATH%" ^
    --win-dir-chooser ^
    --win-shortcut ^
    --win-menu ^
    --win-menu-group "Top Down Sprite Maker" ^
    --description "%NAME% is a tool for customizing and exporting pixel art sprites."
echo 3/3: Built Windows executable with jpackage

endlocal
