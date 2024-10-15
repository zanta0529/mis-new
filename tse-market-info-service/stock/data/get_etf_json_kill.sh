#ps -ef|grep get_etf_json_sub.sh|grep -v grep
pgrep -f get_etf_json_sub.sh | xargs kill -9
pgrep -f get_etf_json_sub_wget.sh | xargs kill -9
pgrep -f get_all_json.sh | xargs kill -9
echo 'Process Killed.'
echo '* Remember to start 2 main shell scripts: get_ohlc_json_main.sh and get_etf_json_main.sh'
