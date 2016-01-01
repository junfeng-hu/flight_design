usage: dbtest
 -a,--action <action>          action to test
                               (login|searchTicket|orderPayTicket|searchHi
                               story|all)
 -c,--count <number>           total operation count (default: 100,000)
    --days <day number>        how long from start date
    --db <classname>           test DB's full classname
 -h,--help                     print help message
    --start <year-month-day>   start date for test
 -t,--target <number>          target ops/sec (default: unlimited)
    --threads <number>         number of threads (default 1)
