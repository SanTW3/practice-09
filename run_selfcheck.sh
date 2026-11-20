#!/bin/sh
set -eu
cd "$(dirname "$0")"
mkdir -p out
find src -name '*.java' > sources.txt
javac --release 21 -encoding UTF-8 -d out @sources.txt
java -cp out ru.edu.pr06.SelfCheck "$@"
