#!/bin/sh-x
cd ~/stock/data
ps -ef > ~/json/get_all_json.tmp
rt=`cat ~/json/get_all_json.tmp |grep get_all_json.sh |wc -l`
if (( $rt < 2 ))
then
    date +%Y/%m/%d-%H:%M:%S >> ~/json/log/etf_sub_json_all.txt.log
    echo 'No job exist! Now Start ......' >> ~/json/log/etf_sub_json_all.txt.log

    date +%Y/%m/%d-%H:%M:%S >> ~/json/log/etn_sub_json_all.txt.log
    echo 'No job exist! Now Start ......' >> ~/json/log/etn_sub_json_all.txt.log

    while true
    do
    sleep 1
        #ETF
        echo -e '\n-------------------' >>  ~/json/log/etf_sub_json_all.txt.log
        date +%H:%M:%S.%s >>  ~/json/log/etf_sub_json_all.txt.log
        echo '{"a1":[' > ~/json/tmp/all_etf.tmp
        typeset -i json_cnt=0
        for json_file in etf_sub_json_*.txt
        do
            if (( $json_cnt > 0 ))
            then
                echo "," >> ~/json/tmp/all_etf.tmp
            fi

            cat "${json_file}" >> ~/json/tmp/all_etf.tmp
            echo ${json_file} >> ~/json/log/etf_sub_json_all.txt.log
            ((json_cnt=json_cnt+1))
        done
        echo etf_cnt:$json_cnt >> ~/json/log/etf_sub_json_all.txt.log
        echo ',{}]}' >>~/json/tmp/all_etf.tmp
        cat ~/json/tmp/all_etf.tmp |tr -d '\r' |tr -d '\n' > ~/json/tmp/all_etf.tmp2
        mv ~/json/tmp/all_etf.tmp2 /home/cloud/stock/data/all_etf.txt

        #ETN
        echo -e '\n-------------------' >>  ~/json/log/etn_sub_json_all.txt.log
        date +%H:%M:%S.%s >>  ~/json/log/etn_sub_json_all.txt.log
        echo '{"a1":[' > ~/json/tmp/all_etn.tmp
        typeset -i json_cnt=0
        for json_file in etn_sub_json_*.txt
        do
            if (( $json_cnt > 0 ))
            then
                echo "," >> ~/json/tmp/all_etn.tmp
            fi

            cat "${json_file}" >> ~/json/tmp/all_etn.tmp
            echo ${json_file} >> ~/json/log/etn_sub_json_all.txt.log
            ((json_cnt=json_cnt+1))
        done
        echo json_cnt:$json_cnt >> ~/json/log/etn_sub_json_all.txt.log
        echo ',{}]}' >>~/json/tmp/all_etn.tmp
        cat ~/json/tmp/all_etn.tmp |tr -d '\r' |tr -d '\n' > ~/json/tmp/all_etn.tmp2
        mv ~/json/tmp/all_etn.tmp2 /home/cloud/stock/data/all_etn.txt

        sleep 3
    done
else
    date +%Y/%m/%d-%H:%M:%S >> ~/json/log/etf_sub_json_all.txt.log
    echo 'check job, ok!' >> ~/json/log/etf_sub_json_all.txt.log

    date +%Y/%m/%d-%H:%M:%S >> ~/json/log/etn_sub_json_all.txt.log
    echo 'check job, ok!' >> ~/json/log/etn_sub_json_all.txt.log
fi


