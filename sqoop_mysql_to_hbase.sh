#!/bin/bash


sudo sqoop import \
    --connect jdbc:mysql://mysql_server/mysql_table \
    --table mysql_table_name \
    --hbase-table hbase_table_name \
    --hbase-row-key mysql_column \
    --column-family metadata \
    --hbase-create-table \
    --username root -P
