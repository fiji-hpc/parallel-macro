#!/bin/bash

path=/scratch/work/project/open-19-3/experiment-org

for file in $path/*/*
do
	identify $file >& /dev/null
	if [ $? -eq 0 ]
	then
		echo "Correct $file"
	else
		echo "!!!! Invalid file: $file"
	fi
done
