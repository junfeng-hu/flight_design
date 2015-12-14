create_namespace 'flight'

user_rowkey = 'username'

create 'flight:user', {NAME => 'i',
                       BLOOMFILTER => 'ROW',
                       BLOCKCACHE => 'true'}

flight_rowkey = 'startplace:arrivalplace:flightdate:idflight'

create 'flight:flight',
       {NAME => 'a',
        BLOOMFILTER => 'NONE',
        BLOCKCACHE => 'false'},
       {NAME => 't',
        BLOOMFILTER => 'ROWCOL',
        COMPRESSION => 'SNAPPY',
        IN_MEMORY => 'true',
        BLOCKCACHE => 'true'}

order_rowkey = 'username:flightdate:idflight'

create 'flight:order',
       {NAME => 'o',
        BLOOMFILTER => 'ROW',
        COMPRESSION => 'SNAPPY',
        BLOCKCACHE => 'false'}

order_cf_o_cq = 'startplace:arrivalplace:starttime:arrivaltime:price:isfirst'
