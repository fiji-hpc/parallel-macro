#!/bin/bash

# The number of compute nodes is limited by the number of views in the dataset:
views=9

# The number to repeat the experiment for a specific number of compute nodes:
repetitions=10

# Budget name:
budget=OPEN-17-47

# Loop through all possible compute node numbers:
for ((compute_nodes_number=1; compute_nodes_number<=$views; compute_nodes_number++))
do
        echo "Compute node number $compute_nodes_number"

        # Repeat ten times to get accurate results:
        for((repetition=1; repetition<=$repetitions; repetition++))
        do
                echo "Repetition number $repetition"
                # Submit the job:
				qsub -A $budget -q qprod -o $HOME/.scijava-parallel/ -e $HOME/.scijava-parallel/ -l select=$compute_nodes_number:ncpus=24:mpiprocs=1:ompthreads=24 -l walltime=00:20:00 ./runCommand.sh
        done
done 