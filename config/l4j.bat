@echo off

set L4J_PATH="C:\Program Files (x86)\Launch4j\launch4j.jar"
set CONFIG="..\out\artifacts\build_config\win_build.xml"

java -jar %L4J_PATH% "%CONFIG%"
