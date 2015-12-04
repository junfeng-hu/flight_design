#!/bin/bash

tar -cJf ./dist/flight_design.tar.xz \
    --exclude-vcs --exclude=ppt --exclude=dist \
    --exclude=*.bak --exclude=*.sh \
    *
