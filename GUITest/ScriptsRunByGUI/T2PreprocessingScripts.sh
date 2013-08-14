#! /bin/bash

imageData=$1  ##/staff/yl13/TestData/NNU996/NNU996_T2.nii.gz
subj=`fsl5.0-remove_ext $imageData`
age=$2

cd ../PreprocessingScripts

cp $imageData ${subj}_original

##Convert image to float

./convert $imageData $imageData -float

##Rescale image

./rescale $imageData $imageData 0 1000 

##brain extract

fsl5.0-bet $imageData ${subj}_brain -R -f 0.3 -m

##/path/NNU996_T2.nii.gz  /path/${subj}_brain 

##Bias correct

./N4 3 -i ${subj}_brain.nii.gz -x ${subj}_brain_mask.nii.gz -o ${subj}_brain.nii.gz -c [50x50x50,0.001] -s 2 -b [100,3] -t [0.15,0.01,200]


##Rescale image

./rescale ${subj}_brain.nii.gz ${subj}_brain.nii.gz 0 1000 

##Registration

./cog ${subj}_brain.nii.gz ./templates/template-${age}.nii.gz ${subj}_template_to_T2.dof
./rreg  ${subj}_brain.nii.gz ./templates/template-${age}.nii.gz -dofin ${subj}_template_to_T2.dof -dofout ${subj}_template_to_T2.dof
./areg  ${subj}_brain.nii.gz ./templates/template-${age}.nii.gz -dofin ${subj}_template_to_T2.dof -dofout ${subj}_template_to_T2.dof
./nreg ${subj}_brain.nii.gz ./templates/template-${age}.nii.gz -dofin ${subj}_template_to_T2.dof -dofout ${subj}_template_to_T2.dof -parin ./nreg_neonate.cnf


./ffdcomposeN ${subj}_T2_to_template.dof -dofin_i ${subj}_template_to_T2.dof

f1=$1
filename=${f1##*/}
result=`./dofprint ${subj}_T2_to_template.dof |grep FFD`

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


