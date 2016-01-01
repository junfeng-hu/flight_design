package cn.edu.fudan.flightsys.dbtest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junfeng on 12/11/15.
 */
public class RecordSleep {
    private static RecordSleep ourInstance = new RecordSleep();
    List<Long> sleepTimes = new ArrayList<>();

    private RecordSleep() {
    }

    public static RecordSleep getInstance() {
        return ourInstance;
    }

    public long sum() {
        long s = 0;
        for (long l : sleepTimes) {
            if (l > 0) {
                s += l;
            }
        }
        return s;
    }

    public synchronized void add(long nanoTime) {
        sleepTimes.add(nanoTime);
    }
}
