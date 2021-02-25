#!/bin/bash
# LIST VERSION

# The number of times to repeat each experiment (default: 10)
repetitions=1

# Budget name:
budget=OPEN-20-21

# Number of nodes:
nodes=8

# Path to Fiji:
pathToFiji="$HOME/Fiji.app/ImageJ-linux64"

# Path to Jython script:
scriptPath="/scratch/work/project/open-19-3/ArtificialDataScripts"

# Script name:
scriptName="processBoth.py"

# Maximum time to run the job:
maxTime="5:00:00"

# Log directory for the .OU and .ER files:
logDirectory="/scratch/work/project/open-19-3/experiment-org-logs-ops-gaps"

# Path to directory containing experiment dataset subdirectories:
experimentDirectory="/scratch/work/project/open-19-3/experiment-org"

# Output directory containing experiment results:
experimentOutputDirectory="/scratch/work/project/open-19-3/experiment-org-output"

# Nodes per group:
nodesPerGroup=$nodes # 1 for Parallel Macro, nodes for OpenMPI Ops and any other number < nodes for combination:

# There is a number of max job limit:
maxJobs=20

echo "Big list experiment runner started. Press ctr+c to cancel (You have 10 seconds!)"
echo "Will use $nodes nodes."
sleep 10s


# Loop a number of times for each combination:
for((repetition=1; repetition<=$repetitions; repetition++))
do
	echo "Repetition number: $repetition"
	# Loop through all possible combinations of file sizes, file number and repetitions.
	# The number of nodes is fixed.
	while IFS= read -r line
	do
		set -- $line
		fileSize=$1
		fileNumber=$2

		echo "File size is: $fileSize and number of files: $fileNumber "
		# Pass the dataset name to the Jython script:
		directory=$(printf "D_%05d_%05d" $fileSize $fileNumber)
		echo "The directory is $directory"
		
		# Prevent this script from reaching the max job submission limit. OR
		# Wait for previous repetition to finish.
		while [[ $(qstat | grep $USER | grep $directory)  ]] || [[ $(qstat | grep $USER | wc -l) -ge $maxJobs ]];
		do
			echo "jobs of this combination are running, will try in 5m"
			sleep 5m
		done

		# Submit the job using the correct subdirectory:
		qsub -A $budget -q qprod -N $directory -o $logDirectory/$directory/ -e $logDirectory/$directory/ -l select=$nodes:ncpus=24:mpiprocs=1:ompthreads=24 -l walltime=$maxTime -v pTF="$pathToFiji",sP="$scriptPath",sN="$scriptName",eD="$experimentDirectory",eOD="$experimentOutputDirectory",d="$directory",fN="$fileNumber",nPG="$nodesPerGroup" ./runCommand.sh

		# Pace jobs
		sleep 5s
	done < runList.txt
done
