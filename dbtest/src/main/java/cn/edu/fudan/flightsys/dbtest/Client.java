package cn.edu.fudan.flightsys.dbtest;

import cn.edu.fudan.flightsys.pojo.City;
import cn.edu.fudan.flightsys.pojo.Ticket;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

/**
 * Created by junfeng on 12/8/15.
 */
public class Client extends Thread {

    public static final long SECOND = (long) Math.pow(10, 9);
    private static final DataProvider dataProvider = DataProvider.getInstance();
    private DB db;
    private long target;
    private long operationCount;
    private EnumAction action;
    private LocalDate startDate;
    private int days;
    private Random random = new Random();

    public Client(DB db, long target, long operationCount,
                  EnumAction action, LocalDate startDate, int days) {
        this.db = db;
        this.target = target;
        this.operationCount = operationCount;
        this.action = action;
        this.startDate = startDate;
        this.days = days;
    }

    @Override
    public void run() {
        super.run();
        try {
            db.init();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        for (long i = 0; i < operationCount; i += target) {
            long st = System.nanoTime();
            for (long j = 0; j < target; ++j) {
                switch (action) {
                    case login: doLoginTest();
                        break;
                    case searchTicket: doSearchTicketTest();
                        break;
                    case orderPayTicket: doOrderPayTicketTest();
                        break;
                    case searchHistory: doSearchHistoryTest();
                        break;
                    case all: doAllTest();
                        break;
                }
            }
            long used = System.nanoTime() - st;
            /*System.out.println(Thread.currentThread().getName()
                    + " current target: "
                    + (double) target * SECOND / used);
            */
            RecordSleep.getInstance().add(SECOND - used);
            while (System.nanoTime() - st < SECOND) {
                // spin sleep
            }

        }
        try {
            db.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean doLoginTest() {
        String username = dataProvider.sampleAUser();
        return db.login(username, username);
    }

    public List<Ticket> doSearchTicketTest() {
        int startIndex = dataProvider.sampleACityIndex();
        City start = dataProvider.getCityUseIndex(startIndex);
        City next = dataProvider.getCityUseIndex(
                dataProvider.sampleNextCityIndex(startIndex)
        );
        LocalDate endDate = startDate.plusDays(random.nextInt(days));
        return db.searchTicket(start.getCityName(), next.getCityName(), startDate, endDate);
    }

    public boolean doOrderPayTicketTest() {
        String username = dataProvider.sampleAUser();
        List<Ticket> tickets = doSearchTicketTest();
        Ticket ticket = tickets.get(random.nextInt(tickets.size()));
        return db.orderPayTicket(username, ticket, random.nextBoolean());
    }

    public void doSearchHistoryTest() {
        String username = dataProvider.sampleAUser();
        LocalDate endDate = startDate.plusDays(random.nextInt(days));
        db.searchHistory(username, startDate, endDate);
    }

    public void doAllTest() {
        String username = dataProvider.sampleAUser();
        db.login(username, username);
        int startIndex = dataProvider.sampleACityIndex();
        City start = dataProvider.getCityUseIndex(startIndex);
        City next = dataProvider.getCityUseIndex(
                dataProvider.sampleNextCityIndex(startIndex)
        );
        LocalDate endDate = startDate.plusDays(random.nextInt(days));
        List<Ticket> tickets = db.searchTicket(start.getCityName(), next.getCityName(), startDate, endDate);
        Ticket ticket = tickets.get(random.nextInt(tickets.size()));
        db.orderPayTicket(username, ticket, random.nextBoolean());
        db.searchHistory(username, startDate, endDate);
    }
}
