#! /bin/bash

### INPUTS NEEDED #################################################################
#subject information
#target age for template - input from GUI
temp_age=$1          ##30
dir_path=$2          ##/staff/yl13/TestDataForT2    

#list of subject names and ages - from GUI?
#image for analysis

#B0 or T2 space

###################################################################################

#skeletonise cortex and create masks

checkForError ()
{
if [ $? != 0 ] || [ $NoError == "false" ]
then
	echo "Process Unsuccessful(error occurred)"
	NoError=false
fi
}

NoError=true
OLDPWD=$(pwd)
cd ../PreprocessingScripts

echo "creating skeletons"
fsl5.0-fslmerge -t ${dir_path}/cortical_analysis/stats/tmp_all_cortex ${dir_path}/cortical_analysis/maps/tmp_*_cortex_map.nii.gz
fsl5.0-fslmaths ${dir_path}/cortical_analysis/stats/tmp_all_cortex -Tmean -s 1 ${dir_path}/cortical_analysis/stats/mean_cortex -odt float
checkForError

rm ${dir_path}/cortical_analysis/maps/tmp_*_cortex_map.nii.gz ${dir_path}/cortical_analysis/stats/tmp_all_cortex.nii.gz
fsl5.0-fslmerge -t ${dir_path}/cortical_analysis/stats/all_cortex ${dir_path}/cortical_analysis/maps/*_cortex_map.nii.gz
checkForError

#skeletonise mean cortex
fsl5.0-tbss_skeleton -i  ${dir_path}/cortical_analysis/stats/mean_cortex -o ${dir_path}/cortical_analysis/stats/mean_cortex_skeleton
fsl5.0-fslmaths ${dir_path}/cortical_analysis/stats/mean_cortex_skeleton -bin ${dir_path}/cortical_analysis/stats/mean_cortex_skeleton_mask
checkForError

#copy over template brain mask
cp ./templates/mask-${temp_age}.nii.gz ${dir_path}/cortical_analysis/stats/template_mask.nii.gz
fsl5.0-fslmaths ${dir_path}/cortical_analysis/stats/template_mask -mul 0 ${dir_path}/cortical_analysis/stats/search_mask
fsl5.0-fslmaths ${dir_path}/cortical_analysis/stats/template_mask -dilD -dilD -dilD ${dir_path}/cortical_analysis/stats/template_mask
checkForError

#create distance map
fsl5.0-fslmaths ${dir_path}/cortical_analysis/stats/template_mask -mul -1 -add 1 -add ${dir_path}/cortical_analysis/stats/mean_cortex_skeleton_mask ${dir_path}/cortical_analysis/stats/mean_cortex_distance
fsl5.0-distancemap -i ${dir_path}/cortical_analysis/stats/mean_cortex_distance -o ${dir_path}/cortical_analysis/stats/mean_cortex_distance
checkForError

fsl5.0-fslmerge -t ${dir_path}/cortical_analysis/stats/all_data ${dir_path}/cortical_analysis/maps/*data_transformed.nii.gz
fsl5.0-tbss_skeleton -i ${dir_path}/cortical_analysis/stats/mean_cortex -p 0 ${dir_path}/cortical_analysis/stats/mean_cortex_distance ${dir_path}/cortical_analysis/stats/search_mask ${dir_path}/cortical_analysis/stats/all_cortex ${dir_path}/cortical_analysis/stats/all_data_skeletonised -a ${dir_path}/cortical_analysis/stats/all_data 
checkForError

f1=$dir_path
filename=${f1##*/}

if $NoError
then
	echo "Process Successful"
	echo "Success" > $2/$filename"_success"
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
	echo "Error" > $2/$filename"_error"
	read
fi

cd $OLDPWD


