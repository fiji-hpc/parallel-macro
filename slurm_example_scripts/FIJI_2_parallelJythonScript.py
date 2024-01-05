from cz.it4i.fiji.parallel_macro import ParallelMacro as PM

mpi_rank = PM.getRank()
mpi_size = PM.getSize()

import subprocess
hostname = subprocess.check_output(['hostname'])

print("FIJI macro on "+hostname+" is # "+str(mpi_rank)+" / "+str(mpi_size))
