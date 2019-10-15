// START OF MAIN
print("Running MPI macro: Hello World Benchmark");

// Size of the image to generate:
width = 5120
height = 5120

// Message to show:
message = "Hello world!";

// Number of times to show hello world on the image:
numberOfMessages = 10000;

// Number of times to add noise to make the message difficult to see:
numberOfTimesToApplyNoise = 10;

// Number of lines to add to make the message difficult to see:
numberOfLines = 10000;

//setTool("text");

// Seed the random generator
random("seed", getTime())

// Parallelization
parInit();
	parAddTask("Message overlay generation.");
	parAddTask("Flatten message overlays.");
	parAddTask("Add noise multiple times.");
	parAddTask("Draw random lines.");
	parAddTask("Gaussian Blur.");
	parReportTasks();
	

	rank = parGetRank();
	size = parGetSize();

	print("My rank = " + rank + ", MPI world size = " + size);
	
	widthSubimage = floor(width/size);
	heightSubimage = floor(height/size);
	
	// Give rank 0 any extra work:
	if(rank == 0){
		widthSubimage = round(widthSubimage + width % size);
		heightSubimage = round(heightSubimage + height % size);
	}
	
	newImage(message, "8-bit black", widthSubimage, heightSubimage, 1);
	
	// Split the number of messages to the processing elements:
	part = floor(numberOfMessages/size);
	
	// Give rank 0 any extra messages:
	if(rank == 0){
		part = round(part + numberOfMessages % size);
	}
	
	print("Rank "+rank+" will create "+part+" hello world overlays, on a "+widthSubimage+" x "+heightSubimage+" subimage.");
	
	counter = 0;
	while(counter < part){
		// Create and show a single hello world:
		fontSize = random * 39 + 1;
		setFont("Serif", fontSize, " antialiased");
		red = random * 255;
		green = random * 255;
		blue = random * 255;
		setColor(red, green, blue);
		xPosition = random * widthSubimage;
		yPosition = random * heightSubimage;
		Overlay.drawString("Hello World!", xPosition, yPosition, 0.0);
		Overlay.show();
		counter++;
		
		// Update progress periodically:
		if(counter % 500 == 0){
			parReportProgress(0, counter/part * 100);
		}
	}
	parReportProgress(0, 100);
	
	parReportProgress(1, 0);
	run("Flatten");
	parReportProgress(1, 100);

	// Add noise to make it difficult to read:
	parReportProgress(2, 0);
	for (i = 0; i < 10; i++) {
		run("Add Noise");
		parReportProgress(2, i/numberOfTimesToApplyNoise * 100);
	}
	parReportProgress(2, 100);
	
	// Add random lines:
	parReportProgress(3, 0);
	for (i = 0; i < numberOfLines; i++) {
		makeLine(random * width, random * height, random * width, random * height);
		red = random * 255;
		green = random * 255;
		blue = random * 255;
		setForegroundColor(red, green, blue);
		run("Draw");
		parReportProgress(3, i/numberOfLines * 100);
	}
	parReportProgress(3, 100);
	
	parReportProgress(4, 0);
	run("Gaussian Blur...", "sigma=1");
	parReportProgress(4,100);
	
	exec("mkdir output");
	exec("chmod -R 777 output");
	path = ".//output//";
	imageTitle = "HelloWorld";	
	saveAs("tiff",path+imageTitle+rank);
	
	// Report that the rank has finished it's work:
	parReportText("Rank "+rank+" finished.");
	
	// Sync
	parBarrier();
// Stop parallel
parFinalize();