#!/bin/bash

# The maximum number of compute nodes to use:
maximum_compute_nodes=1

# The number to repeat the experiment for a specific number of compute nodes:
repetitions=1

# Budget name:
budget=OPEN-19-3

# Loop through all possible compute node numbers:
for ((compute_nodes_number=1; compute_nodes_number<=$maximum_compute_nodes; compute_nodes_number++))
do
	echo "Compute node number $compute_nodes_number"

	# Repeat ten times to get accurate results:
	for((repetition=1; repetition<=$repetitions; repetition++))
	do
		echo "Repetition number: $repetition"
		# Submit the job:
		qsub -A $budget -q qprod -o $HOME/.scijava-parallel/ -e $HOME/.scijava-parallel/ -l select=$compute_nodes_number:ncpus=24:mpiprocs=1:ompthreads=24 -l walltime=00:20:00 ./runCommand.sh
		echo "Will wait for 25 minutes before submitting another job."

		# Wait 25 minutes to avoid submitting too many jobs at once.
		sleep 25m
	done
done 