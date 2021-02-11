echo "Fake data and directories generator"
for fileSize in 1 2 4 8 16 32 64 128 256
do
	for fileNumber in 1 2 4 8 16 32 64 128 256 512 1024
	do 
		echo "Filesize is: $fileSize and number of files: $fileNumber "
		# Generate the fake directories with the correct name:
		directory=$(printf "D_%05d_%05d" $fileSize $fileNumber)
		mkdir ./$directory
		
		# Generate fake files:
		for file in $(seq "$fileNumber")
		do
			fileName=$(printf "img_t_%05d.tif" $file)
			touch ./$directory/$fileName
		done
	done
done
