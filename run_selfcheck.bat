@echo off
cd /d "%~dp0"
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac --release 21 -encoding UTF-8 -d out @sources.txt
if errorlevel 1 exit /b 1
java -cp out ru.edu.pr06.SelfCheck %*
