while true
do

    sleep 5

scp -p futures_side.txt futures_chart.txt all_etf.txt 10.2.120.78:/home/cloud/stock/data
scp -p futures_side.txt futures_chart.txt all_etf.txt 10.212.120.77:/home/cloud/stock/data
scp -p futures_side.txt futures_chart.txt all_etf.txt 10.212.120.78:/home/cloud/stock/data

done

