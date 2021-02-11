#!/bin/bash

# The number of times to repeat each experiment
repetitions=10

# Budget name:
budget=none

# Number of nodes:
nodes=8

# Set of file sizes:
fileSizes="1 2 4 8 16 32 64 128 256"

# Set of file numbers:
fileNumbers="1 2 4 8 16 32 64 128 256 512 1024"

# Path to Fiji:
pathToFiji="/Applications/Fiji.app/Contents/MacOS/ImageJ-macosx"

# Path to Jython script:
scriptPath="/Users/stefanos/Documents/FijiScripts"

# Script name:
scriptName="dummyProcessBoth.py"

# Maximum time to run the job:
maxTime="2:00:00"

# Log directory for the .OU and .ER files:
logDirectory="$HOME/logs"

# Path to directory containing experiment dataset subdirectories:
experimentDirectory="none"

# Output directory containing experiment results:
experimentOutputDirectory="none"

# Nodes per group:
nodesPerGroup=1 # 1 for Parallel Macro, nodes for OpenMPI Ops and any other number < nodes for combination:

echo "Big experiment runner started. Press ctr+c to cancel (You have 10 seconds!)"
echo "Will use $nodes nodes."
sleep 10s


# Loop a number of times for each combination:
for((repetition=1; repetition<=$repetitions; repetition++))
do
	echo "Repetition number: $repetition"
	# Wait for previous repetition to finish.
	while $(false) # stat -u $USER | grep $directory
	do
		echo "jobs are running, will try in 5m"
		sleep 5m
	done

	# Loop through all possible combinations of file sizes, file number and repetitions.
	# The number of nodes is fixed.
	for fileSize in $fileSizes
	do
		for fileNumber in $fileNumbers
		do 
			echo "File size is: $fileSize and number of files: $fileNumber "
			# Pass the dataset name to the Jython script:
			directory=$(printf "D_%05d_%05d" $fileSize $fileNumber)
			echo "The directory is $directory"
		
			# Load the OpenMPI module:
			## ml OpenMPI/4.0.0-GCC-6.3.0-2.27

			# Submit the job using the correct subdirectory:
			$pathToFiji --ij2 --headless --console --run $scriptPath/$scriptName 'inputFolder="'$experimentDirectory'",outputFolder="'$experimentOutputDirectory'",filesNumber="'$fileSize'",nodesPerGroup="'$nodesPerGroup'"'

			# Pace jobs
			#sleep 5s
		done
	done
done
