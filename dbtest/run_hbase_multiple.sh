#!/bin/bash

if [[ $# < 1 ]]; then
    echo "$0 action [ --arg value ]*";
    exit 1;
fi

./run_hbase.sh -a $1 -c 800000 --start 2015-12-14 --days 7 --threads 8 $@
