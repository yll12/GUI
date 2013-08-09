#!/bin/bash

#INPUT
#need to define imageData, age

imageData=$1
subj=`fsl5.0-remove_ext $imageData`
age=$2
imageDatadir=$3

scriptsdir=`pwd`/../PreprocessingScripts

cd $imageDatadir

f1=$1
filename=${f1##*/}

result=`$scriptsdir/dofprint ${subj}_T2_to_template.dof |grep FFD`
if [ "$result" == "" ];
then 
	echo "Preprocessing has not correctly finished!"; 
	echo "Error" > $imageDatadir/$filename"_error"
	read;
	exit 0;
fi


#SCRIPT starts here
#=====================================================================================

sdir=segmentations-data

mkdir $sdir $sdir/template $sdir/logs $sdir/tissue-posteriors



echo "creating $imageData tissue priors"

dof=${imageData}_template_to_T2.dof

$scriptsdir/transformation $scriptsdir/atlas-7/structure1/$age.nii.gz $sdir/template/$imageData-csf.nii.gz -dofin $dof -target ${imageData}_brain.nii.gz -linear 
$scriptsdir/transformation $scriptsdir/atlas-7/structure2/$age.nii.gz $sdir/template/$imageData-gm.nii.gz -dofin $dof -target ${imageData}_brain.nii.gz -linear 
$scriptsdir/transformation $scriptsdir/atlas-7/structure3/$age.nii.gz $sdir/template/$imageData-wm.nii.gz -dofin $dof -target ${imageData}_brain.nii.gz -linear 
$scriptsdir/transformation $scriptsdir/atlas-7/structure4/$age.nii.gz $sdir/template/$imageData-outlier.nii.gz -dofin $dof -target ${imageData}_brain.nii.gz -linear 
$scriptsdir/transformation $scriptsdir/atlas-7/structure5/$age.nii.gz $sdir/template/$imageData-ventricles.nii.gz -dofin $dof -target ${imageData}_brain.nii.gz -linear 
$scriptsdir/transformation $scriptsdir/atlas-7/structure6/$age.nii.gz $sdir/template/$imageData-cerebstem.nii.gz -dofin $dof -target ${imageData}_brain.nii.gz -linear 
$scriptsdir/transformation $scriptsdir/atlas-7/structure7/$age.nii.gz $sdir/template/$imageData-dgm.nii.gz -dofin $dof -target ${imageData}_brain.nii.gz -linear 


$scriptsdir/ems_neonatal ${imageData}_brain.nii.gz 7 $sdir/template/$imageData-csf.nii.gz $sdir/template/$imageData-gm.nii.gz $sdir/template/$imageData-wm.nii.gz $sdir/template/$imageData-outlier.nii.gz $sdir/template/$imageData-ventricles.nii.gz $sdir/template/$imageData-cerebstem.nii.gz $sdir/template/$imageData-dgm.nii.gz $sdir/$imageData-initial-tissues.nii.gz -padding 0 -mrf $scriptsdir/conn_tissues_ven_cstem_dgm.mrf -hui 1 3 1 0 1 1 1 2  -relaxtimes 2 -saveprob 0 $sdir/tissue-posteriors/$imageData-csf-just.nii.gz -saveprob 1 $sdir/tissue-posteriors/$imageData-gm.nii.gz -saveprob 2 $sdir/tissue-posteriors/$imageData-wm.nii.gz -saveprob 3 $sdir/tissue-posteriors/$imageData-outlier.nii.gz -saveprob 4 $sdir/tissue-posteriors/$imageData-ventricles.nii.gz 1>$sdir/logs/"$imageData"_tissue_labels-em 2>$sdir/logs/"$imageData"_tissue_labels-em-err 






fsl5.0-fslmaths $sdir/tissue-posteriors/$imageData-csf-just.nii.gz -add $sdir/tissue-posteriors/$imageData-ventricles.nii.gz -fmean -s 1 -mul 100 $sdir/tissue-posteriors/$imageData-csf-blur.nii.gz
fsl5.0-fslmaths $sdir/tissue-posteriors/$imageData-gm.nii.gz -fmean -s 1 -mul 100 $sdir/tissue-posteriors/$imageData-gm-blur.nii.gz
fsl5.0-fslmaths $sdir/tissue-posteriors/$imageData-wm.nii.gz -fmean -s 1 -mul 100 $sdir/tissue-posteriors/$imageData-wm-blur.nii.gz
fsl5.0-fslmaths $sdir/tissue-posteriors/$imageData-outlier.nii.gz -fmean -s 1 -mul 100 $sdir/tissue-posteriors/$imageData-outlier-blur.nii.gz









#----------------------------------------------------------------------------#



mkdir -p $sdir/labels/bg $sdir/MADs
for (( r=1; r<=87; r++ ));do mkdir $sdir/labels/seg$r;done




echo "transforming atlas labels for $imageData"
#split $sdir/labels

for (( r=1; r<=50; r++ )); do 
	if [ $r == 42 ];then continue;fi	
	if [ $r == 43 ];then continue;fi
	$scriptsdir/transformation $scriptsdir/atlas-50/structure$r/$age.nii.gz $sdir/labels/seg$r/$imageData.nii.gz -target ${imageData}_brain.nii.gz -dofin $dof -linear
done
$scriptsdir/transformation $scriptsdir/atlas-87/structure42/$age.nii.gz $sdir/labels/seg42/$imageData.nii.gz -target ${imageData}_brain.nii.gz -dofin $dof -linear
$scriptsdir/transformation $scriptsdir/atlas-87/structure43/$age.nii.gz $sdir/labels/seg43/$imageData.nii.gz -target ${imageData}_brain.nii.gz -dofin $dof -linear
$scriptsdir/transformation $scriptsdir/atlas-87/structure85/$age.nii.gz $sdir/labels/seg85/$imageData.nii.gz -target ${imageData}_brain.nii.gz -dofin $dof -linear
$scriptsdir/transformation $scriptsdir/atlas-87/structure86/$age.nii.gz $sdir/labels/seg86/$imageData.nii.gz -target ${imageData}_brain.nii.gz -dofin $dof -linear
$scriptsdir/transformation $scriptsdir/atlas-87/structure87/$age.nii.gz $sdir/labels/seg87/$imageData.nii.gz -target ${imageData}_brain.nii.gz -dofin $dof -linear

$scriptsdir/transformation $scriptsdir/atlas-50/bg/$age.nii.gz $sdir/labels/bg/$imageData.nii.gz -target ${imageData}_brain.nii.gz -dofin $dof -linear


$scriptsdir/transformation $scriptsdir/csfparts/csfpart-$age.nii.gz $sdir/template/$imageData-csfpart.nii.gz -target ${imageData}_brain.nii.gz -dofin $dof -linear 
$scriptsdir/transformation $scriptsdir/csfparts/lvenpart-$age.nii.gz $sdir/template/$imageData-lvenpart.nii.gz -target ${imageData}_brain.nii.gz -dofin $dof -linear 
$scriptsdir/transformation $scriptsdir/csfparts/rvenpart-$age.nii.gz $sdir/template/$imageData-rvenpart.nii.gz -target ${imageData}_brain.nii.gz -dofin $dof -linear 








echo "preparing $sdir labels"
addem=""

fsl5.0-fslmaths $sdir/tissue-posteriors/$imageData-csf-blur.nii.gz -add $sdir/tissue-posteriors/$imageData-outlier-blur.nii.gz $sdir/labels/bg/$imageData-outcsf-sum.nii.gz 

fsl5.0-fslmaths $sdir/labels/bg/$imageData.nii.gz -mul $sdir/tissue-posteriors/$imageData-csf-blur.nii.gz -div $sdir/labels/bg/$imageData-outcsf-sum.nii.gz -thr 0 $sdir/labels/seg83/$imageData.nii.gz 
fsl5.0-fslmaths $sdir/labels/bg/$imageData.nii.gz -mul $sdir/tissue-posteriors/$imageData-outlier-blur.nii.gz -div $sdir/labels/bg/$imageData-outcsf-sum.nii.gz -thr 0 $sdir/labels/seg84/$imageData.nii.gz 





cortseg=$scriptsdir/cortical.seg
corts=`cat $cortseg |cut -f 1`

#dividing each cortical structure into csf,gm,wm part
val=50
for r in ${corts}; do
val=$(($val+1))
#wm part->wm
fsl5.0-fslmaths $sdir/labels/seg$r/$imageData.nii.gz  -mul $sdir/tissue-posteriors/$imageData-wm-blur.nii.gz -div 100 $sdir/labels/seg$val/$imageData.nii.gz -odt float 
#csf part->csf,lven,rven
fsl5.0-fslmaths $sdir/labels/seg$r/$imageData.nii.gz  -mul $sdir/tissue-posteriors/$imageData-csf-blur.nii.gz -div 100 -mul $sdir/template/$imageData-csfpart.nii.gz  -add $sdir/labels/seg83/$imageData.nii.gz $sdir/labels/seg83/$imageData.nii.gz -odt float 
fsl5.0-fslmaths $sdir/labels/seg$r/$imageData.nii.gz  -mul $sdir/tissue-posteriors/$imageData-csf-blur.nii.gz -div 100 -mul $sdir/template/$imageData-rvenpart.nii.gz  -add $sdir/labels/seg50/$imageData.nii.gz $sdir/labels/seg50/$imageData.nii.gz -odt float 
fsl5.0-fslmaths $sdir/labels/seg$r/$imageData.nii.gz  -mul $sdir/tissue-posteriors/$imageData-csf-blur.nii.gz -div 100 -mul $sdir/template/$imageData-lvenpart.nii.gz  -add $sdir/labels/seg49/$imageData.nii.gz $sdir/labels/seg49/$imageData.nii.gz -odt float 
#gm part->gm
fsl5.0-fslmaths $sdir/labels/seg$r/$imageData.nii.gz  -mul $sdir/tissue-posteriors/$imageData-gm-blur.nii.gz -div 100 $sdir/labels/seg$r/$imageData.nii.gz -odt float 
done





#create MAD
inds=`cat $scriptsdir/indm.csv|sed -e 's:,: :g'`
inds="$inds 85 86 87"

if [ ! -f $sdir/$imageData-MAD.nii.gz ];then 
nn=11

$scriptsdir/grad3D ${imageData}_brain.nii.gz $sdir/$imageData-grad.nii.gz 0
fsl5.0-fslmaths $sdir/$imageData-grad.nii.gz -kernel boxv $nn -fmedian $sdir/$imageData-MAD1.nii.gz -odt float
fslmaths $sdir/$imageData-grad.nii.gz -sub $sdir/$imageData-MAD1.nii.gz -abs $sdir/$imageData-MAD2.nii.gz
fsl5.0-fslmaths $sdir/$imageData-MAD2.nii.gz -kernel boxv $nn -fmedian $sdir/$imageData-MAD1.nii.gz -odt float
fsl5.0-fslmaths $sdir/$imageData-grad.nii.gz -div $sdir/$imageData-MAD1.nii.gz -div 1.4826 -sqr -mul 0.5 -add 1 -log $sdir/$imageData-MAD2.nii.gz
fsl5.0-fslmaths $sdir/$imageData-MAD2.nii.gz -mas ${imageData}_brain.nii.gz -add 1 -recip $sdir/$imageData-MAD.nii.gz;
rm $sdir/$imageData-MAD2.nii.gz $sdir/$imageData-MAD1.nii.gz
fi

str=""; for i in ${inds};do str="$str-add $sdir/labels/seg$i/$imageData.nii.gz "; done
str=`echo $str| sed -e 's:^\-add ::g'`
fsl5.0-fslmaths $str -div 100 -mul $sdir/$imageData-MAD.nii.gz $sdir/$imageData-MAD-subspace.nii.gz 







#--------------------------------------------------------------------------------------#


mkdir segmentations


cortseg=$scriptsdir/cortical.seg
corts=`cat $cortseg |cut -f 1`
numcorts=`echo $corts|wc -w`

super1=$numcorts
for r in ${corts}; do
super1="$super1 "$(($r-1))
done
super2=$numcorts
for (( r=50; r<82; r++ )); do
super2="$super2 $r"
done


echo "running EM segmentation for $imageData"
structs=""; 
for (( r=1; r<=87; r++ ));do 
structs="$structs $sdir/labels/seg$r/$imageData.nii.gz";
done

$scriptsdir/ems_neonatal ${imageData}_brain.nii.gz 87 $structs segmentations/"$imageData"_all_labels.nii.gz -padding 0 -norelax  -superlabel $super1 -superlabel $super2 -mrf $scriptsdir/connectivities.mrf -hui 1 83 1 82 $super1 $super2  -postpenalty $sdir/$imageData-MAD-subspace.nii.gz  1>$sdir/logs/$imageData-em 2>$sdir/logs/$imageData-em-err







echo "postprocessing labels for $imageData"

fslmaths segmentations/"$imageData"_all_labels.nii.gz -uthr 82 segmentations/"$imageData"_labels.nii.gz

$scriptsdir/padding segmentations/"$imageData"_labels.nii.gz segmentations/"$imageData"_all_labels.nii.gz segmentations/"$imageData"_labels.nii.gz 86 42
$scriptsdir/padding segmentations/"$imageData"_labels.nii.gz segmentations/"$imageData"_all_labels.nii.gz segmentations/"$imageData"_labels.nii.gz 87 43

val=50;
for r in ${corts}; do
	val=$(($val+1))
	$scriptsdir/padding segmentations/"$imageData"_labels.nii.gz segmentations/"$imageData"_labels.nii.gz segmentations/"$imageData"_labels.nii.gz $val $r
done




fsl5.0-fslmaths segmentations/"$imageData"_all_labels.nii.gz -thr 51 -uthr 82 -bin -mul 3 segmentations/"$imageData"_tissue_labels.nii.gz
$scriptsdir/multipadding segmentations/"$imageData"_tissue_labels.nii.gz segmentations/"$imageData"_all_labels.nii.gz segmentations/"$imageData"_tissue_labels.nii.gz 32 $corts 2
$scriptsdir/padding segmentations/"$imageData"_tissue_labels.nii.gz segmentations/"$imageData"_all_labels.nii.gz segmentations/"$imageData"_tissue_labels.nii.gz 83 1
$scriptsdir/padding segmentations/"$imageData"_tissue_labels.nii.gz segmentations/"$imageData"_all_labels.nii.gz segmentations/"$imageData"_tissue_labels.nii.gz 84 4
$scriptsdir/multipadding segmentations/"$imageData"_tissue_labels.nii.gz segmentations/"$imageData"_all_labels.nii.gz segmentations/"$imageData"_tissue_labels.nii.gz 2 49 50 5
$scriptsdir/multipadding segmentations/"$imageData"_tissue_labels.nii.gz segmentations/"$imageData"_all_labels.nii.gz segmentations/"$imageData"_tissue_labels.nii.gz 3 17 18 19 6
$scriptsdir/multipadding segmentations/"$imageData"_tissue_labels.nii.gz segmentations/"$imageData"_all_labels.nii.gz segmentations/"$imageData"_tissue_labels.nii.gz 16 1 2 3 4 40 41 42 43 44 45 46 47 48 85 86 87 7

rm -r $sdir

echo "finished $imageData!!"

if [ -f $imageDatadir/segmentations/"$imageData"_tissue_labels.nii.gz ];
then 
	echo "Process Successful"
	echo "Success" > $imageDatadir/$filename"_success"
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
	echo "Error" > $imageDatadir/$filename"_error"
	echo "Segmentation has not correctly finished!"; 
	read;
fi



