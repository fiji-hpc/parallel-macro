# Budget name:
budget=OPEN-17-47

qsub -A $budget -q qprod -o $HOME/.scijava-parallel/ -e $HOME/.scijava-parallel/ -l select=1:ncpus=24:mpiprocs=1:ompthreads=24 -l walltime=00:20:00 ./runCommand.sh