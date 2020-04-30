# Number of nodes to run the script on:
nodes=5

qsub -q qexp -o $HOME/.scijava-parallel/ -e $HOME/.scijava-parallel/ -l select=$nodes:ncpus=24:mpiprocs=1:ompthreads=24 -l walltime=00:20:00 ./runCommand.sh