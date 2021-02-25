These are some sample scripts that submit jobs for all datasets in an HPC cluster.

The "submitJobsList.sh" uses a list instead that is provided by the user in a file called runList.txt,
instead of using the internal for loop of the script "submitJobs.sh". 
Two numbers must be written in the list fileSize, fileNumber separated by a single space and then a new line. 
See "runList.txt" for an example.