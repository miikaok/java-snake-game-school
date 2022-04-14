@echo off

echo Compiling...
javac -d ./build ./src/snakegame/*.java

cd ./build/

java snakegame.Game