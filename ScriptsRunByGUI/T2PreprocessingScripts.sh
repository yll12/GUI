#! /bin/bash

imageData=$1  ##/staff/yl13/TestData/NNU996/JIJFIJADFIT2llohk.nii.gz
age=$2
fullpath=$3   ##/staff/yl13/TestData/NNU996
subID=$4      ##NNU996
subj=${fullpath}/${subID}_       ##/staff/yl13/TestData/NNU996/NNU996_

if [ ! ${subj}T2.nii.gz == $1 ]; then
	cp $imageData ${subj}T2.nii.gz
	imageData=${subj}T2.nii.gz
fi


cd ../PreprocessingScripts


if [ ! ${subj}original.nii.gz == $imageData ]; then
	cp $imageData ${subj}original.nii.gz
fi

if [ ! ${subj}T2.nii.gz == $imageData ]; then
	cp $imageData ${subj}T2.nii.gz
fi

##Convert image to float

./convert ${subj}T2.nii.gz ${subj}T2.nii.gz -float

##Rescale image

./rescale ${subj}T2.nii.gz ${subj}T2.nii.gz 0 1000 

##brain extract

fsl5.0-bet ${subj}T2.nii.gz ${subj}brain -R -f 0.3 -m

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


