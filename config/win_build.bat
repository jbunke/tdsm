@echo off

setlocal

set "ID=tdsm"
set "NAME=Top Down Sprite Maker"
set "DEV=Jordan Bunke"

echo Building %NAME% for Windows...

set "ARTIFACT_DIR=..\out\artifacts\release"
set "OUTPUT_DIR=..\out\artifacts\dist\win"

rd /s /q "%OUTPUT_DIR%\build"
rd /s /q "%OUTPUT_DIR%\runtime"
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

:: Analyze Java module dependencies
jdeps --multi-release 17 --print-module-deps "%ARTIFACT_DIR%\tdsm.jar" > "%OUTPUT_DIR%\modules.txt"
echo 1/4: Analyzed dependencies

:: Read modules from file
set /p MODULES=<"%OUTPUT_DIR%\modules.txt"
echo Modules: %MODULES%

:: Generate runtime image
set JAVA_MOD_PATH="C:\Program Files\Java\jdk-17\jmods"

jlink --module-path %JAVA_MOD_PATH% --add-modules "%MODULES%" --output "%OUTPUT_DIR%\runtime"
echo 2/4: Generated runtime image

:: Build the EXE with Launch4J
:: call l4j.bat
:: echo 3/4: Built Windows executable with Launch4J

:: Compile installer script
:: iscc installer_script.iss
:: echo 4/4: Compiled Windows installer script

set "VERSION_FILE=..\res\version"
set /p VERSION=<"%VERSION_FILE%"

set ICON_PATH=".\icons\win-icon.ico"

jpackage ^
    --type exe ^
    --input "%ARTIFACT_DIR%" ^
    --dest "%OUTPUT_DIR%\installer" ^
    --name "%NAME%" ^
    --app-version "%VERSION%" ^
    --main-jar tdsm.jar ^
    --runtime-image "%OUTPUT_DIR%\runtime" ^
    --icon "%ICON_PATH%" ^
    --vendor "%DEV%" ^
    --win-dir-chooser ^
    --win-shortcut ^
    --win-menu ^
    --win-menu-group "Top Down Sprite Maker" ^
    --description "%NAME%"
move "%OUTPUT_DIR%\installer\%NAME%-%VERSION%.exe" "%OUTPUT_DIR%\installer\%ID%-%VERSION%-installer.exe"

echo 3/4: Built Windows installer with jpackage

jpackage ^
    --type app-image ^
    --input "%ARTIFACT_DIR%" ^
    --dest "%OUTPUT_DIR%\build" ^
    --name "%ID%" ^
    --app-version "%VERSION%" ^
    --main-jar tdsm.jar ^
    --runtime-image "%OUTPUT_DIR%\runtime" ^
    --icon "%ICON_PATH%" ^
    --vendor "%DEV%" ^
    --description "%NAME%"

echo 4/4: Built Windows executable with jpackage

endlocal
