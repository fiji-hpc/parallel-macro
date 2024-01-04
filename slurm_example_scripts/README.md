## How to run locally

If one has a functional local installation of (Open)MPI, things can be tried out
locally, without accessing any cluster at all.

> To test for a functional MPI environment, try running `mpirun -np 3 hostname`.
> It should list a host name (which could very well be just `localhost`) on
> three lines.

For example, the most straightforward example is the shell script that just
prints out the OpenMPI variables with the current rank ID and size of the MPI
world. This is MPI-specifics: world size says how many parallel instances are
executed in this MPI session, and the rank is "position"/"order" of an instance
within this world; consequently, world size must be the same while rank must be
unique in the prints from individual instances.

```
$> mpirun -np 4 sbatch_1_worker_BASH.sh

BASH ========= localhost.localdomain is 0 / 4 =========
BASH ========= localhost.localdomain is 2 / 4 =========
BASH ========= localhost.localdomain is 1 / 4 =========
BASH ========= localhost.localdomain is 3 / 4 =========
```

Similarly, using the `mpirun -np N` where `N` says how many parallel instances
should MPI create, one can examine the Python code, and the variants for Fiji.

## How to run where a SLURM scheduler is available (and functional)

Benefiting from a cluster environment, one can ask its scheduler (which today
is most often the SLURM) to grab a couple of cluster compute nodes, setup a MPI
world context across the nodes, and start our given process in this. Already the
first step, the grabbing of nodes, can happen in two regimes.

### In interactive (live) mode

Here, one asks SLURM to provide the nodes and to start a shell session on one of
them. Within the session, one can issue commands as usual (hence the name
*interactive*), including `srun` or `mpirun`.

Depending on the configuration of the particular cluster, one most likely needs
to know the accounting "token" (a name of an account that tells to the cluster
what and how much is available to the user) and a name of the scheduling queue.

Then one needs to decide for oneself, given the number of tasks to be computed,
how many compute nodes (Ns) would be needed and how many tasks (Ts) can run in
parallel on one such compute node.

Then the interactive session can be started as follows

```
salloc -A account_name -p queue -N Ns -n Ns*Ts
```

where `account_name` and `queue` should be replaced with the appropriate names,
`Ns` and the result of `Ns*Ts` (this is a multiplication formula) should be
replaced with numbers.

Eventually a session is opened, command line prompt becomes available. Then, one
has to make sure that an (Open)MPI environment is accessible. On cluster, a
`module` system is typically available, and an (Open)MPI module would be hard
not to have there at hand. In order to access the MPI, issue

```
module add OpenMPI
```

and `mpirun` should become available to the whole thing:

```
$> mpirun sbatch_1_worker_BASH.sh

BASH ========= cn044.karolina.it4i.cz is 1 / 4 =========
BASH ========= cn044.karolina.it4i.cz is 0 / 4 =========
BASH ========= cn694.karolina.it4i.cz is 2 / 4 =========
BASH ========= cn694.karolina.it4i.cz is 3 / 4 =========
```

Notice that the parameter `-np` is no longer provided (because the number of MPI
parallel instances has been told already to the scheduler (with `-N -n`) who has
setup the MPI accordingly), and that the output list shows two different host
names (that's the two different compute nodes, `Ns=2`), each repeated twice (that's
because `Ts=2` in our example).

### In batch mode

The batch mode is the same from the scheduling point of view, except that no
live (interactive) bash session is provided to the user. Instead, SLURM wants a
script to be executed (as if one would execute it from the live session) and
starts it when all compute nodes become available.

The scheduler thus needs to know (at least) the same parameters for the
scheduling and to set up the compute environment, and the script file. One can
provide the parameters on the command line, or, more conveniently, as a
special-kind of comment (`#SBATCH `) in the script file that's going to be
executed. Checkout the file `sbatch_0_iface.sh`, modify it, and test it

```
sbatch sbatch_0_iface.sh
```

