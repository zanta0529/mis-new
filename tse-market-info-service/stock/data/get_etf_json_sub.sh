#! /bin/sh
#$0 get_etf_json_sub.sh
#$1 filename to save to
#$2 timer to sleep
#$3 web address
#$4 web address(2)

echo '$0:'$0
echo '$1:'$1
echo '$2:'$2
echo '$3:'$3
echo '$4:'$4

rt=`ps -ef|grep -v 'grep' |grep $0 | grep $1 |wc -l`
#ps -ef|grep -v 'grep' |grep $0 | grep $1 |wc -l

echo $rt
if (( $rt <= 2 ))
then
    while true
    do
        echo start get $3
        #wget --timeout=15 -t 1 $3 $4 --no-cache -O /home/cloud/json/tmp/$1.tmp -a /home/cloud/json/log/$1.log -nv 
        curl --insecure --connect-timeout 15 --retry 1 $3  -o /home/cloud/json/tmp/$1.tmp  --stderr /home/cloud/json/log/$1.log
        sleep 1
        if [ `grep \"0000\" /home/cloud/json/tmp/$1.tmp |wc -l` == 0 ]
        then
            echo  'wait'
        else
            mv /home/cloud/json/tmp/$1.tmp /home/cloud/stock/data/$1
        fi
        sleep $2
    done
else
    echo job exist!
fi
