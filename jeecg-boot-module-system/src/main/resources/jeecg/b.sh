readfileName=$1 #/root/cmdc/data/input/110000/scm_nretail_ipw_002127113114006000_110000.csv
timefilePath=$2#/root/cmdc/data/predict/1100/scm_nretail_ipw_002127113114006000_1100.csv
outfileName=$3 #/root/cmdc/data/predict/110000/scm_nretail_ipw_002127113114006000_110000.csv
logfileName=$4 #/root/cmdc/log/scm_nretail_ipw_002127113114006000_110000.log
dependRate=$5
#echo "/opt/anaconda3/bin/python /root/cmdc/bin/python/main_online_go.py -i ${readfileName} -o ${outfileName} 44 |tee -a ${logfileName}"
/opt/anaconda3/bin/python /root/cmdc/bin/python/main_go.py -i ${readfileName}  -t ${timefilePath} -o ${outfileName}  ${dependRate} |tee -a ${logfileName}
