#!/bin/bash

now=`date +%Y-%m-%d-%H-%M-%S`
tar -cJf "./dist/flight_design-${now}.tar.xz" \
    --exclude-vcs --exclude=ppt --exclude=dist \
    --exclude=*.bak --exclude=*.sh \
    *
