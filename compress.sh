#!/bin/bash

now=`date +%Y-%m-%d-%H-%M-%S`
tar -cJf "./dist/flight_design-${now}.tar.xz" \
    --exclude-vcs --exclude=ppt --exclude=dist \
    --exclude=byliuhuan --exclude=dbtest --exclude=webapp \
    --exclude=*.bak --exclude=*.sh \
    *
