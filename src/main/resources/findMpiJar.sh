#!/bin/bash

# Set the locations to be the environment variable.
locations=$LD_LIBRARY_PATH;

# Set colon as the delimiter
IFS=':';

# Split into words
read -a strarr <<< "$locations";

# Searching for mpi.jar in all paths:
for path in "${strarr[@]}"
do
  # Attempt to find the file needed:
  found=$(find $path -name 'mpi.jar');
  if [ ! -z "$found" ]
  then
    break;
  fi
done
echo "$found"
