#!/bin/sh

for ff in etf_sub_json_*.txt
do
  if [ "$ff" == "etf_sub_json_1.txt" ];then
    iconv -t utf8 -f big5 etf_sub_json_1.txt > etf_sub_json_1.txt.tmp
    mv etf_sub_json_1.txt.tmp etf_sub_json_1.txt
  fi
done

