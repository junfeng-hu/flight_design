package cn.edu.fudan.flightsys.pojo;

/**
 * Created by junfeng on 12/6/15.
 */
public class Airplane {
    private int id;
    private long firstClass;
    private long economyClass;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getFirstClass() {
        return firstClass;
    }

    public void setFirstClass(long firstClass) {
        this.firstClass = firstClass;
    }

    public long getEconomyClass() {
        return economyClass;
    }

    public void setEconomyClass(long economyClass) {
        this.economyClass = economyClass;
    }

    @Override
    public String toString() {
        return "Airplane{" +
                "id=" + id +
                ", firstClass=" + firstClass +
                ", economyClass=" + economyClass +
                '}';
    }
}
