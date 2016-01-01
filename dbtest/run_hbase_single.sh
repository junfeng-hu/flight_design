#!/bin/bash

if [ $# != 1 ]; then
    echo "$0 action";
    exit 1;
fi

./run_hbase.sh -a $1 --start 2015-12-14 --days 7
