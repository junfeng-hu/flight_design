## Baseline

### simple Baseline
total operations=100000, total time used(nano)=3394257, averaged delay time(nano)=33.942570

### max targeted Baseline
threadsNumber=1
targetPerThread=100001
countPerThread=100001
total operations=100001, total time used(nano)=4187038
throughput(per second)=23883470.845022, averaged delay time(nano)=41.869961


### complete client max targeted Baseline
set target equals to totalCount
threadsNumber=1
targetPerThread=100001
countPerThread=100001
total operations=100001, total time used(nano)=10325578
throughput(per second)=9684784.716168, averaged delay time(nano)=103.254747

## HBase

### single thread

#### login
total operations=100001, total time used(nano)=57189034094
throughput(per second)=1748.604459, averaged delay time(nano)=.622094

#### searchTicket
total operations=100001, total time used(nano)=160807217185
throughput(per second)=621.868855, averaged delay time(nano)=1608056.091289

#### orderPayTicket
total operations=100001, total time used(nano)=512432836756
throughput(per second)=195.149477, averaged delay time(nano)=5124277.124789

#### searchHistory
total operations=100001, total time used(nano)=234650229727
throughput(per second)=426.170476, averaged delay time(nano)=2346478.832482

#### all
total operations=100001, total time used(nano)=903081462495
throughput(per second)=110.733089, averaged delay time(nano)=9030724.317707



### multiple threads

8 threads

#### login
total operations=800008, total time used(nano)=55376927870
throughput(per second)=14446.594110, averaged delay time(nano)=553763.741064

#### searchTicket
total operations=800008, total time used(nano)=224498115244
throughput(per second)=3563.539939, averaged delay time(nano)=2244958.702856

#### orderPayTicket
total operations=800008, total time used(nano)=485855468317
throughput(per second)=1646.596678, averaged delay time(nano)=4858506.098112

#### searchHistory
total operations=800008, total time used(nano)=236491042531
throughput(per second)=3382.825799, averaged delay time(nano)=2364886.776442

#### all
total operations=800008, total time used(nano)=763237071652
throughput(per second)=1048.177597, averaged delay time(nano)=7632294.393576


20 threads

#### login
total operations=1000020, total time used(nano)=31188534720
throughput(per second)=32063.705749, averaged delay time(nano)=623758.219236

#### searchTicket
total operations=1000020, total time used(nano)=191752529771
throughput(per second)=5215.159358, averaged delay time(nano)=3834973.895942

#### orderPayTicket
total operations=1000020, total time used(nano)=304195709405
throughput(per second)=3287.423093, averaged delay time(nano)=6083792.512250

#### searchHistory
total operations=1000020, total time used(nano)=414626456866
throughput(per second)=2411.857670, averaged delay time(nano)=8292363.290054

#### all

