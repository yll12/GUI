#! /bin/bash

imageData=$1
subj=`fsl5.0-remove_ext $imageData`
age=$2


cp $imageData ${subj}_original

##Convert image to float

PreprocessingScripts/convert $imageData $imageData -float

##Rescale image

PreprocessingScripts/rescale $imageData $imageData 0 1000 

##brain extract

fsl5.0-bet $imageData ${subj}_brain -R -f 0.3 -m

##/path/NNU996_T2.nii.gz  /path/${subj}_brain 

##Bias correct

PreprocessingScripts/N4 3 -i ${subj}_brain.nii.gz -x ${subj}_brain_mask.nii.gz -o ${subj}_brain.nii.gz -c [50x50x50,0.001] -s 2 -b [100,3] -t [0.15,0.01,200]


##Rescale image

PreprocessingScripts/rescale ${subj}_brain.nii.gz ${subj}_brain.nii.gz 0 1000 

##Registration

PreprocessingScripts/cog ${subj}_brain.nii.gz PreprocessingScripts/templates/template-${age}.nii.gz ${subj}_template_to_T2.dof
PreprocessingScripts/rreg  ${subj}_brain.nii.gz PreprocessingScripts/templates/template-${age}.nii.gz -dofin ${subj}_template_to_T2.dof -dofout ${subj}_template_to_T2.dof
PreprocessingScripts/areg  ${subj}_brain.nii.gz PreprocessingScripts/templates/template-${age}.nii.gz -dofin ${subj}_template_to_T2.dof -dofout ${subj}_template_to_T2.dof
PreprocessingScripts/nreg ${subj}_brain.nii.gz PreprocessingScripts/templates/template-${age}.nii.gz -dofin ${subj}_template_to_T2.dof -dofout ${subj}_template_to_T2.dof -parin PreprocessingScripts/nreg_neonate.cnf


PreprocessingScripts/ffdcomposeN ${subj}_T2_to_template.dof -dofin_i ${subj}_template_to_T2.dof

f1=$1
filename=${f1##*/}
result=`PreprocessingScripts/dofprint ${subj}_T2_to_template.dof |grep FFD`

if [ "$result" == "" ];
then 
	echo "Preprocessing has not correctly finished!"; 
	echo "Error" > $3/$filename"_error"
	read
else
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
fi


