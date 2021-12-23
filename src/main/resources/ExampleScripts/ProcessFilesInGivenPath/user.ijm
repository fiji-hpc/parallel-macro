// Example: Process a list of files.

// Start of user's code:
function parGetMySubList(list) {
	rank = parGetRank();
	size = parGetSize();
	lastRank = round(size)-1;
	if (rank == lastRank) 
		subList = Array.slice(list, (rank) * round(list.length / size) , list.length);
	else
	    subList = Array.slice(list, (rank) * round(list.length / size) , (rank + 1) * round(list.length / size));
	return subList;
}

function countFiles(path) {
  list = getFileList(path); 
  for (i=0; i<list.length; i++) {
	  if (endsWith(list[i], "/"))
		countFiles(""+path+list[i]);
	  else
		count++;
  }
}

function processFiles(dir) {
	list = getFileList(dir);
	subList = parGetMySubList(list);

	for (i=0; i<subList.length; i++) {
		if (endsWith(subList[i], "/"))
			processFiles(""+dir+subList[i]);
		else {
			//showProgress(n++, count);
			path = dir+subList[i];
			processFile(path);
		}
	}
}

function processFile(path) {
	if (endsWith(path, ".tif")) {
		print("#" + rank + " node processing " + path);		
		//open(path);
		//run("Subtract Background...", "rolling=50 white");
		//save(path);
		//close();
		processed++;
	}
}

// Main
print("Running MPI macro");

// Add all tasks:
printTask = parAddTask("Print your rank and MPI world size.");
countFilesTask = parAddTask("Count files.");
processFilesTask = parAddTask("Process files task.");
printCountedFilesTask = parAddTask("Print number of files counted.");

// Report all tasks:
parReportTasks();

// Report that the first task started:
parReportProgress(printTask, 0);

// Get the rank and size of the node:
rank = parGetRank();
size = parGetSize();

message = "My rank = " + rank + ", MPI world size = " + size;
print(message);
parReportText(message);

// Report that first task is completed:
parReportProgress(printTask, 100);

// Sychronization of all the nodes:
parBarrier();

// Report that the second task started:
parReportProgress(countFilesTask, 0);

// Count files:
count = 0;
processed = 0;
path = "~/test_data/"
countFiles(path);
print("count = " + count + "\n");

// Report that the second task is completed:
parReportProgress(countFilesTask, 100);

// Another synchronization:
parBarrier();

// Report that the third task started:
parReportProgress(processFilesTask, 0);

processFiles(path);

// Report that the third task is completed:
parReportProgress(processFilesTask, 100);

// Another synchronization:
parBarrier();

// Report that the last task started:
parReportProgress(printCountedFilesTask, 0);

print("#" + rank + " node - " +processed+" files processed\n");

// Report that the third task is completed:
parReportProgress(printCountedFilesTask, 100);
