#!/bin/bash

## $1 :: Input Data ##e.g: /staff/yl13/TestData/NNU996/unreg-data.nii.gz
## $2 :: Boolean (true if and only if bedpostx is ticked) 
## $3 :: Input Directory ##e.g: /staff/yl13/TestData/NNU996
## $4 :: bvecs
## $5 :: bvals

error ()
{
if [ $? != 0 ] || [ $NoError == "false" ]
then
	echo "Process Unsuccessful(error occurred)"
	NoError=false
fi
}
NoError=true
cd ../PreprocessingScripts
./eddy_correct_neonate2 $1 $3/data.nii.gz 0 
error
./fdt_rotate_bvecs $3/$4 $3/rotated_bvecs $3/data.ecclog
error
fsl5.0-fslroi $3/data.nii.gz $3/nodif 0 1 
error
fsl5.0-bet $3/nodif.nii.gz $3/nodif_brain -f 0.3 -m 
error
fsl5.0-dtifit -k $3/data.nii.gz -o $3/dti -r $3/rotated_bvecs -b $3/$5 -m $3/nodif_brain_mask
error
if $2; then

	fsl5.0-bedpostx $3

fi
error

f1=$1
filename=${f1##*/}

if $NoError
then
	echo "Process Successful"
	echo "Success" > $3/$filename"_success"
	echo "Exit terminal in . . ."
	x=10
	while [ $x -ge 0 ]
		do 
			sleep 1
			echo $x
			x=$((x - 1))
		done 
	exit 0
else
	echo "Error" > $3/$filename"_error"
	read
fi
