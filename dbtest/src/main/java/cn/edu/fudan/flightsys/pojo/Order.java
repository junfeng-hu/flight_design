package cn.edu.fudan.flightsys.pojo;

/**
 * Created by junfeng on 12/5/15.
 */
public class Order {
    private String username;
    private Ticket ticket;
    private long orderTime;
    private boolean status;
    private boolean isFirst;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    @Override
    public String toString() {
        return "Order{" +
                "username='" + username + '\'' +
                ", ticket=" + ticket +
                ", orderTime=" + orderTime +
                ", status=" + status +
                ", isFirst=" + isFirst +
                '}';
    }
}
