#!/bin/bash
for node in $(seq 2  9)
do
        # Compare serial results with all other results
        # for different number of nodes:
        if [[ -d "$HOME/CLUSTER_TEST/OUTPUT/$node" ]]; then
                echo "Number of nodes: $node"
                for f in $HOME/CLUSTER_TEST/OUTPUT/1/*tif*
                do
                        filename="$(basename $f)"
                        echo "=========="
                        echo "* File: $filename"
                        newpath="$HOME/CLUSTER_TEST/OUTPUT/$node/$filename"
                        printf "$newpath\n$f\n"
                        cmp $f $newpath
                done
        fi
done