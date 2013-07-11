#!/bin/bash

## $1 :: Input Data
## $2 :: Boolean (True if and only if bedpostx is ticked) 
## $3 :: Input Directory
## $4 :: bvecs
## $5 :: bvals

cd ../PreprocessingScripts

./eddy_correct_neonate2 $1 $3/data.nii.gz 0 

./fdt_rotate_bvecs $3/$4 $3/rotated_bvecs $3/data.ecclog

fsl5.0-fslroi $3/data.nii.gz $3/nodif 0 1 
fsl5.0-bet $3/nodif.nii.gz $3/nodif_brain -f 0.3 -m 
fsl5.0-dtifit -k $3/data.nii.gz -o $3/dti -r $3/rotated_bvecs -b $3/$5 -m $3/nodif_brain_mask

if $2; then

	fsl5.0-bedpostx $3

fi

cd $OLDPWD

