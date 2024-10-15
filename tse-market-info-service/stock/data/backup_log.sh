#!/bin/sh
find /home/cloud/json/log -type f -name '*.txt.log' -exec mv {} {}.`date +%w` \;
