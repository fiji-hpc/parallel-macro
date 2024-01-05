from cz.it4i.fiji.parallel_macro import PM

mpi_rank = PM.getRank()
mpi_size = PM.getSize()

import subprocess
hostname = subprocess.check_output(['hostname'])

print("FIJI macro on "+hostname+" is # "+mpi_rank+" / "+mpi_size)
