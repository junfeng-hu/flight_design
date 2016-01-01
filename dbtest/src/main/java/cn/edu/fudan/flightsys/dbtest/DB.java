package cn.edu.fudan.flightsys.dbtest;

import cn.edu.fudan.flightsys.pojo.Order;
import cn.edu.fudan.flightsys.pojo.Ticket;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by junfeng on 12/5/15.
 */
public abstract class DB {

    public void init() throws IOException {
    }

    public void cleanup() throws IOException {
    }

    public abstract boolean login(String username, String password);

    public abstract List<Ticket> searchTicket(String startPlace, String endPlace, LocalDate startDate, LocalDate endDate);

    public abstract boolean orderPayTicket(String username, Ticket ticket, boolean isFirst);

    public abstract void searchHistory(String username, LocalDate startDate, LocalDate endDate);

}
