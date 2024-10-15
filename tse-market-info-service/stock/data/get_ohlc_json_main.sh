#! /bin/sh-x
cd /home/cloud/stock/data
typeset i x=0
while read var1
do
    (( x = x+1 ))
#    echo call:$x $var1
    if [[ ${var1} != "#"* && ${var1} != "//"*  ]] ;then
        /bin/sh ./get_etf_json_sub.sh $var1 2>&1 &
    fi
#    echo called:$x $var1
done <WEB_LIST_mis

typeset i x=0
while read var1
do
    (( x = x+1 ))
#    echo call:$x $var1
    if [[ ${var1} != "#"* && ${var1} != "//"*  ]] ;then
        /bin/sh ./get_etf_json_sub_wget.sh $var1 2>&1 &
    fi
#    echo called:$x $var1
done <WEB_LIST_mis_wget