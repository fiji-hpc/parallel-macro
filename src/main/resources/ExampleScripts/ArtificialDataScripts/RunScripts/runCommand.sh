# Load the OpenMPI module:
ml OpenMPI/4.0.0-GCC-6.3.0-2.27

mpirun $pTF --ij2 --headless --console --run $sP/$sN 'inputFolder="'$eD/$d/'",outputFolder="'$eOD/$d/'",filesNumber="'$fN'",nodesPerGroup="'$nPG'"'
