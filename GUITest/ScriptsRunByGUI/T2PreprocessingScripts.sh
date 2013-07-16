#! /bin/bash

inputT2 /staff/yl13/Data/NNU996/NNU996_T2.nii.gz
subj=`fsl5.0-remove_ext /staff/yl13/Data/NNU996/NNU996_T2.nii.gz`



cp <<inputT2>> ${subj}_original

##Convert image to float

PreprocessingScripts/convert <<inputT2>> <<inputT2>> -float

##Rescale image

PreprocessingScripts/rescale <<inputT2>> <<inputT2>> 0 1000 

##brain extract

fsl5.0-bet <<inputT2>> ${subj}_brain -R -f 0.3 -m

##/path/NNU996_T2.nii.gz  /path/${subj}_brain 

##`echo /staff/yl13/Data/NNU996/NNU996_T2.nii.gz | cut -d "." -f 1`_brain
##`fsl5.0-remove_ext /staff/yl13/Data/NNU996/NNU996_T2.nii.gz`_brain


##Bias correct

#PreprocessingScripts/N4 3 -i ${subj}_brain.nii.gz -x ${subj}_brain_mask.nii.gz -o ${subj}_brain.nii.gz -c [50x50x50,0.001] -s 2 -b [100,3] -t [0.15,0.01,200]


##Rescale image

PreprocessingScripts/rescale ${subj}_brain.nii.gz ${subj}_brain.nii.gz 0 1000 

##Registration

#PreprocessingScripts/cog - need from Antonios
PreprocessingScripts/cog  ${subj}_brain.nii.gz PreprocessingScripts/templates/template-(age).nii.gz ${subj}_template_to_T2.dof
PreprocessingScripts/rreg  ${subj}_brain.nii.gz PreprocessingScripts/templates/template-(age).nii.gz -dofin ${subj}_template_to_T2.dof -dofout ${subj}_template_to_T2.dof
PreprocessingScripts/areg  ${subj}_brain.nii.gz PreprocessingScripts/templates/template-(age).nii.gz -dofin ${subj}_template_to_T2.dof -dofout ${subj}_template_to_T2.dof
PreprocessingScripts/nreg ${subj}_brain.nii.gz PreprocessingScripts/templates/template-(age).nii.gz -dofin ${subj}_template_to_T2.dof -dofout ${subj}_template_to_T2.dof -parin PreprocessingScripts/nreg_neonate.cnf


#PreprocessingScripts/ffdcomposeN ${subj}_T2_to_template.dof -dofin_i ${subj}_template_to_T2.dof





