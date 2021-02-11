# This script is used to generate fake logs to text the parsing script
# which is used to get the results and format them for use in gnu plot.

# Number of repetitions:
repetitions=10

echo "Fake data and directories generator"
for fileSize in 1 2 4 8 16 32 64 128 256
do
	for fileNumber in 1 2 4 8 16 32 64 128 256 512 1024
	do 
		echo "Filesize is: $fileSize and number of files: $fileNumber "
		# Generate the fake directories with the correct name:
		directory=$(printf "D_%05d_%05d" $fileSize $fileNumber)
		mkdir ./$directory
		
		# Generate fake logs:
		for repetition in $(seq "$repetitions")
		do
			fileName="$directory-$repetition.OU"
			printf "My rank: 0 and size: 1\nTotal execution time: 296.434000015 seconds for 1 nodes." > ./$directory/$fileName
		done
	done
done
