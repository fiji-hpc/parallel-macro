# Python Parallel Logger

## About

This is a simple implementation of the parallel logger (both CSV and XML) of Parallel Macro that can be used to display progress with HPC Workflow Manager from parallelized python programs that create the progress logs needed.

The easiest way to parallelize python programs in a cluster is to use an implementation of the Message Passing Interface (MPI) standard. To do this with python you can use the [mpi4pi](https://mpi4py.github.io/) package (MPI for Python) which provides bindings for MPI implementations such as [Open MPI](https://www.open-mpi.org/). 

## Installation

You must have Open MPI installed on your cluster. If it is possible to install packages on your cluster you can first install mpi4pi in order to use multi-node parallelism provided by MPI in python like so:

```bash

sudo apt update 
sudo apt install python3.8
sudo apt install python3-pip
python -m pip install mpi4py
mpiexec -n 3 python3 /mnt/shared/helloworld.py
```

Where the file referenced in the last line (path `/mnt/shared/python/helloworld.py`) is a small example to test that the installation was successful and has the following content:

```python
from mpi4py import MPI

comm = MPI.COMM_WORLD
rank = comm.Get_rank()

print("Id is "+str(rank)+".")
```

It should print similar output:

````text
Id is 0
Id is 1
````

For more installation information visit the [documentation of mpi4py](https://mpi4py.readthedocs.io/en/stable/install.html).

Now you can use the provided files to report progress on tasks you want to define.

## How to report progress in python

In order to produce progress reports compatible with the HPC Workflow Manager you need to use `python_progress_logger.py` in your python mpi4py program.

To showcase this, a skeleton program is provided called `helloworld.py`. You may use it as a starting point for your program. It uses mpi4py to achieve parallelism and the file provided to create two tasks (described as "My first task" and "My second task"), it sets the first tasks progress first to 50%, then to 100% and finally the second task to 100%.

Here is a snippet from the script:

```python
pl = XmlParallelLogger(rank, size) # Comment out this line to use CSV instead.
#pl = CsvParallelLogger(rank, size) # Uncomment this line to use CSV instead.
pl.add_task("My first task")
pl.add_task("My second task")
pl.report_tasks()
pl.report_progress(0, 50)
pl.report_progress(0, 100)
pl.report_progress(1, 100)
```

## How to use with HPC Workflow Manager

From the main menu of Fiji the click on `Plugins` > `HPC-ParallelTools` > `Paradigm Profiles Configurator`.

Create or edit a paradigm profile of type `HPCWorkflowJobManager` and manager `SSH` as described in the [short guide](https://github.com/fiji-hpc/parallel-macro/wiki/How-to-set-up-the-paradigm-for-Parallel-Macro) but make the following changes:

* In the text field `Remote directory with Fiji` input the the text `python3 /mnt/shared/python/helloworld.py ` make sure it ends in a whitespace. 
* Uncheck `Automatic detection` checkbox. 
* Click `Advance settings` link.
  * In the field `Job Scheduler`, set the scheduler of your remote cluster.
  * Set `Remote ImageJ command` to an empty space `  `.
  * Set the name of the Open MPI of your cluster that you are going to use.
* Finally when creating a job select an empty Jython script in a new directory.
* Run the job.
