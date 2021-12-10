#!/usr/bin/env bash
#
# This script builds required libraries and copies into 
#   Fiji installation directory.
#
#Usage: do-release <Directory with Fiji installation>
#
set -e

cd "$( dirname "${BASH_SOURCE[0]}" )"

mvn compile
mvn dependency:build-classpath -Dmdep.outputFile=classpath
sed -i "1s;^;$PWD/target/classes/:;" classpath

if [ $# -eq 0 ]; then
    fiji="$HOME/Fiji.app"
else
	fiji=$1
fi

mvn -Dscijava.app.directory="$fiji"
echo "installed in $fiji"
