// START OF MAIN
print("Running MPI macro: Scatter example");

// Parallelization
parInit();
	addTask("Create array to send.");
	addTask("Scatter.");
	reportTasks();
	
	rank = parGetRank();
	size = parGetSize();

	print("My rank = " + rank + ", MPI world size = " + size);
	
	// Have rank 0 create the data to scatter:
	parReportProgress(0, 0);
	
	if(rank == 0){
		sendArray = newArray(10, 20, 30, 40);
	} else {
		sendArray = newArray(0);
	}
	
	parReportProgress(0, 100);
	
	parReportProgress(1, 0);
	receiveArray = parScatterEqually(sendArray, 4, 0);
	parReportProgress(1, 100);
	
	// Print received array:
	for(i = 0; i < lengthOf(receiveArray); i++){
		print("rank"+rank+" element: "+receiveArray[i]);
	}
	
	// Sync
	parBarrier();
// Stop parallel
parFinalize();