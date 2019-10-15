// START OF MAIN
print("Running MPI macro: Scatter example");

// Parallelization
parInit();
	createArrayTask = parAddTask("Create array to send.");
	scatterTask = parAddTask("Scatter.");
	parReportTasks();
	
	rank = parGetRank();
	size = parGetSize();

	print("My rank = " + rank + ", MPI world size = " + size);
	
	// Have rank 0 create the data to scatter:
	parReportProgress(createArrayTask, 0);
	
	// Only rank 0 has the data:
	sendArray = newArray(0);
	if(rank == 0){
		sendArray = newArray(10, 20, 30, 40);
	}
	
	parReportProgress(createArrayTask, 100);
	
	parReportProgress(scatterTask, 0);
	// The sendArray length must be known to the other nodes:
	receiveArray = parScatterEqually(sendArray, 4, 0);
	parReportProgress(scatterTask, 100);
	
	// Print received array:
	for(i = 0; i < lengthOf(receiveArray); i++){
		print("rank"+rank+" element: "+receiveArray[i]);
	}
	
	// Sync
	parBarrier();
// Stop parallel
parFinalize();