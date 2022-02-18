# Very simple example of the ParallelLogger used with OpenMPI by mpi4py.
from mpi4py import MPI
from python_progress_logger import ParallelLogger, XmlParallelLogger, CsvParallelLogger

comm = MPI.COMM_WORLD
rank = comm.Get_rank()
size = comm.Get_size()

print("Id is "+str(rank)+". Size "+str(size))
pl = XmlParallelLogger(rank, size) #pl = CsvParallelLogger(rank, size)
pl.add_task("My first task")
pl.add_task("My second task")
pl.report_tasks()
pl.report_progress(0, 50)
pl.report_progress(0, 100)
pl.report_progress(1, 100)
