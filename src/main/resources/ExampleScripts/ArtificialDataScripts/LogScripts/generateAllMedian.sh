# Gets all of the median values from all the dataset directories
# and lists them along with file size and file number:
rm ./allMedian.dat
tail -n 1 ./*/data.dat | sed 's/[^[0-9\.\-]]*/ /g;s/ \+/ /g;s/^ \+\| \+$//g' |
 tr -s ' ' | awk 'NR % 3 != 0' | awk 'NR%2{printf "%s ",$0;next;}1' |
  sed 's/^0*//g' | sed 's/ 0*/ /g' | sed 's/\.[[:blank:]]//g' > ./allMedian.dat
