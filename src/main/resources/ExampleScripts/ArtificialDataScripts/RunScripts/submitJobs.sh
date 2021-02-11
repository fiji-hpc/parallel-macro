#!/bin/bash

# The number of times to repeat each experiment (default: 10)
repetitions=1

# Budget name:
budget=OPEN-19-3

# Number of nodes:
nodes=8

# Set of file sizes (default: "1 2 4 8 16 32 64 128 256"):
fileSizes="1 2 4 8 16 32 64 128 256"

# Set of file numbers (default: "1 2 4 8 16 32 64 128 256 512 1024"):
fileNumbers="1 2 4 8 16 32 64 128 256 512 1024"

# Path to Fiji:
pathToFiji="$HOME/Fiji.app/ImageJ-linux64"

# Path to Jython script:
scriptPath="/scratch/work/project/open-19-3/ArtificialDataScripts"

# Script name:
scriptName="processBoth.py"

# Maximum time to run the job:
maxTime="01:00:00"

# Log directory for the .OU and .ER files:
logDirectory="/scratch/work/project/open-19-3/experiment-org-logs"

# Path to directory containing experiment dataset subdirectories:
experimentDirectory="/scratch/work/project/open-19-3/experiment-org"

# Output directory containing experiment results:
experimentOutputDirectory="/scratch/work/project/open-19-3/experiment-org-output"

# Nodes per group:
nodesPerGroup=1 # 1 for Parallel Macro, nodes for OpenMPI Ops and any other number < nodes for combination:

# There is a number of max job limit (+5):
maxJobs=9

echo "Big experiment runner started. Press ctr+c to cancel (You have 10 seconds!)"
echo "Will use $nodes nodes."
sleep 10s


# Loop a number of times for each combination:
for((repetition=1; repetition<=$repetitions; repetition++))
do
	echo "Repetition number: $repetition"
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
			
			# Prevent this script from reaching the max job submission limit. OR
			# Wait for previous repetition to finish.
			while [[ $(qstat -u $USER | grep $directory)  ]] || [[ $(qstat -u $USER | wc -l) -gt $maxJobs ]];
			do
				echo "jobs of this combination are running, will try in 5m"
				sleep 5m
			done

			# Submit the job using the correct subdirectory:
			qsub -N $directory -q qexp -o $logDirectory/$directory/ -e $logDirectory/$directory/ -l select=$nodes:ncpus=24:mpiprocs=1:ompthreads=24 -l walltime=$maxTime -v pTF="$pathToFiji",sP="$scriptPath",sN="$scriptName",eD="$experimentDirectory",eOD="$experimentOutputDirectory",d="$directory",fN="$fileNumber",nPG="$nodesPerGroup" ./runCommand.sh

			# Pace jobs
			sleep 5s
		done
	done
done
