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

fsl5.0-fslmaths ${scan}/segmentations/${subj}_T2_tissue_labels -thr 2 -uthr 2 -bin ${scan}/segmentations/${subj}_cortex
./transformation ${scan}/segmentations/${subj}_cortex.nii.gz ${dir_path}/cortical_analysis/maps/tmp_${subj}_cortex_map.nii.gz -dofin ${scan}/${subj}_final_template_to_T2.dof -target ./templates/template-${temp_age}.nii.gz -invert  >/dev/null 2>&1
fsl5.0-fslmaths ${dir_path}/cortical_analysis/maps/tmp_${subj}_cortex_map.nii.gz -s 1 ${dir_path}/cortical_analysis/maps/${subj}_cortex_map.nii.gz -odt float


./transformation $INPUT ${dir_path}/cortical_analysis/maps/${subj}_data_transformed -dofin ${scan}/${subj}_final_template_to_T2.dof -target ./templates/template-${temp_age}.nii.gz -invert >/dev/null 2>&1

#IF input image is in diffusion space
#PERFORM B0 to T2 registration
#fslroi data B0 01
#bet?
#cog
#rreg
#areg
#nreg T2 to B0
#ffdcomposeN invert with template registration

#transformation $INPUT etc etc

#FI

#END LOOP

cd $OLDPWD

#START LOOP
#tbss_skeleton -i stats/mean_cortex -p 0 stats/mean_cortex_distance stats/search_mask maps/${scan}_cortex_map maps/${INPUT}_skeletonised -a maps/$INPUT_transformed
#END LOOP
#fsl5.0-fslmerge -t stats/all_$INPUT maps/$INPUT_transformed

exit 0
##CHECK CORTICAL SKELETON EDIT IF NECESSARY##
