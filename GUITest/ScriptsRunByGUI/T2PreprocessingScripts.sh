#! /bin/bash

imageData=$1  ##/staff/yl13/TestData/NNU996/NNU996_T2.nii.gz
age=$2
subj=$4       ##/staff/yl13/TestData/NNU996/NNU996_

cd ../PreprocessingScripts

cp $imageData ${subj}original.nii.gz

##Convert image to float

./convert $imageData $imageData -float

##Rescale image

./rescale $imageData $imageData 0 1000 

##brain extract

fsl5.0-bet $imageData ${subj}brain -R -f 0.3 -m

##/path/NNU996_T2.nii.gz  /path/${subj}brain 

##Bias correct

./N4 3 -i ${subj}brain.nii.gz -x ${subj}brain_mask.nii.gz -o ${subj}brain.nii.gz -c [50x50x50,0.001] -s 2 -b [100,3] -t [0.15,0.01,200]


##Rescale image

./rescale ${subj}brain.nii.gz ${subj}brain.nii.gz 0 1000 

##Registration

./cog ${subj}brain.nii.gz ./templates/template-${age}.nii.gz ${subj}template_to_T2.dof
./rreg  ${subj}brain.nii.gz ./templates/template-${age}.nii.gz -dofin ${subj}template_to_T2.dof -dofout ${subj}template_to_T2.dof
./areg  ${subj}brain.nii.gz ./templates/template-${age}.nii.gz -dofin ${subj}template_to_T2.dof -dofout ${subj}template_to_T2.dof
./nreg ${subj}brain.nii.gz ./templates/template-${age}.nii.gz -dofin ${subj}template_to_T2.dof -dofout ${subj}template_to_T2.dof -parin ./nreg_neonate.cnf


./ffdcomposeN ${subj}T2_to_template.dof -dofin_i ${subj}template_to_T2.dof

f1=$1
filename=${f1##*/}
result=`./dofprint ${subj}T2_to_template.dof |grep FFD`

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


