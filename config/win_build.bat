@echo off

setlocal

set NAME="Top Down Sprite Maker"
set ARTIFACT_DIR="..\out\artifacts\release"
set OUTPUT_DIR="..\out\artifacts\dist\win"

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
call l4j.bat
echo 3/4: Built %NAME% for Windows

:: Compile installer script
iscc installer_script.iss

endlocal
