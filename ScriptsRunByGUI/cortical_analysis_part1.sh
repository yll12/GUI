#! /bin/bash

### INPUTS NEEDED #################################################################
#subject information
#target age for template - input from GUI
temp_age=$1      ##30

#list of subject names and ages - from GUI?
#image for analysis

#B0 or T2 space

###################################################################################
dir_path=$2       ##/staff/yl13/TestDataForT2    
scan=$3           ##/staff/yl13/TestDataForT2/ABC1000
subj=$4           ##ABC1000
age=$5            ##40

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

#check where these are made
mkdir -p ${dir_path}/cortical_analysis/maps ${dir_path}/cortical_analysis/stats  

#compose registrations to target age
#is age odd or even
odev=$(( $age % 2 ))

#if even..
if [ $odev -eq 0 ]; then

    #and less than the template age...
    if [ $age -lt $temp_age ] ; then

        next_age=$age
        dofs=""
        until [ $next_age -eq $temp_age ]; do
            prev_age=$next_age
            diff_age=`awk 'BEGIN { printf "%.0f\n", '$temp_age'-'$next_age' }'`

            if [ $diff_age -gt 2 ]; then
                let next_age=$next_age+2
                dofs="$dofs -dofin ./dofs/${prev_age}-${next_age}.dof.gz"
            else
                let next_age=$next_age+1
                dofs="$dofs -dofin ./dofs/${prev_age}-${next_age}.dof.gz"
            fi

        done

    #if even and more than the template age...
    else

        next_age=$age
        dofs=""
        until [ $next_age -eq $temp_age ]; do
            prev_age=$next_age
            diff_age=`awk 'BEGIN { printf "%.0f\n", '$next_age'-'$temp_age' }'`

            if [ $diff_age -gt 2 ]; then
                let next_age=$next_age-2
                dofs="$dofs -dofin ./dofs/${prev_age}-${next_age}.dof.gz"
            else
                let next_age=$next_age-1
                dofs="$dofs -dofin ./dofs/${prev_age}-${next_age}.dof.gz"
            fi

        done

    fi

#else if odd...
else

    #and less than the template age...
    if [ $age -lt $temp_age ] ; then

        let next_age=$age+1
        dofs="-dofin ./dofs/${age}-${next_age}.dof.gz"

        until [ $next_age -eq $temp_age ]; do
            prev_age=$next_age
            diff_age=`awk 'BEGIN { printf "%.0f\n", '$temp_age'-'$next_age' }'`

            if [ $diff_age -gt 2 ]; then
                let next_age=$next_age+2
                dofs="$dofs -dofin ./dofs/${prev_age}-${next_age}.dof.gz"
            else
                let next_age=$next_age+1
                dofs="$dofs -dofin ./dofs/${prev_age}-${next_age}.dof.gz"
            fi

        done

    #if odd and more than the template age
    else

        let next_age=$age-1
        dofs="-dofin ./dofs/${age}-${next_age}.dof.gz"

        until [ $next_age -eq $temp_age ]; do
            prev_age=$next_age
            diff_age=`awk 'BEGIN { printf "%.0f\n", '$next_age'-'$temp_age' }'`

            if [ $diff_age -gt 2 ]; then
                let next_age=$next_age-2
                dofs="$dofs -dofin ./dofs/${prev_age}-${next_age}.dof.gz"
            else
                let next_age=$next_age-1
                dofs="$dofs -dofin ./dofs/${prev_age}-${next_age}.dof.gz"
            fi

        done

    fi
fi

./ffdcomposeN ${scan}/${subj}_final_template_to_T2.dof -dofin ${scan}/${subj}_template_to_T2.dof `echo $dofs`
checkForError

fsl5.0-fslmaths ${scan}/segmentations/${subj}_tissue_labels -thr 2 -uthr 2 -bin ${scan}/segmentations/${subj}_cortex
./transformation ${scan}/segmentations/${subj}_cortex.nii.gz ${dir_path}/cortical_analysis/maps/tmp_${subj}_cortex_map.nii.gz -dofin ${scan}/${subj}_final_template_to_T2.dof -target ./templates/template-${temp_age}.nii.gz -invert 
fsl5.0-fslmaths ${dir_path}/cortical_analysis/maps/tmp_${subj}_cortex_map.nii.gz -s 1 ${dir_path}/cortical_analysis/maps/${subj}_cortex_map.nii.gz -odt float
checkForError

#IF input image is in diffusion space
#PERFORM B0 to T2 registration

if $7; then

	if [ ! -f ${scan}/${subj}_final_template_to_B0.dof ] ; then
		./cog ${scan}/nodif_brain.nii.gz ${scan}/${subj}_brain.nii.gz  ${scan}/${subj}_T2_to_B0.dof    
		./rreg ${scan}/nodif_brain.nii.gz ${scan}/${subj}_brain.nii.gz  -dofin ${scan}/${subj}_T2_to_B0.dof -dofout ${scan}/${subj}_T2_to_B0.dof
		./areg  ${scan}/nodif_brain.nii.gz ${scan}/${subj}_brain.nii.gz -dofin ${scan}/${subj}_T2_to_B0.dof -dofout ${scan}/${subj}_T2_to_B0.dof
		./nreg ${scan}/nodif_brain.nii.gz ${scan}/${subj}_brain.nii.gz -dofin ${scan}/${subj}_T2_to_B0.dof -dofout ${scan}/${subj}_T2_to_B0.dof -parin ./nreg_neonate.cnf

		./ffdcomposeN ${scan}/${subj}_B0_to_T2.dof -dofin_i ${scan}/${subj}_T2_to_B0.dof
		./ffdcomposeN ${scan}/${subj}_final_template_to_B0.dof -dofin ${scan}/${subj}_T2_to_B0.dof -dofin ${scan}/${subj}_final_template_to_T2.dof

	fi

./transformation $6 ${dir_path}/cortical_analysis/maps/${subj}_data_transformed.nii.gz -dofin ${scan}/${subj}_final_template_to_B0.dof -target ./templates/template-${temp_age}.nii.gz -invert

else

./transformation $6 ${dir_path}/cortical_analysis/maps/${subj}_data_transformed.nii.gz -dofin ${scan}/${subj}_final_template_to_T2.dof -target ./templates/template-${temp_age}.nii.gz -invert 

fi
checkForError

f1=$6
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


cd $OLDPWD

exit 0
##CHECK CORTICAL SKELETON EDIT IF NECESSARY##
