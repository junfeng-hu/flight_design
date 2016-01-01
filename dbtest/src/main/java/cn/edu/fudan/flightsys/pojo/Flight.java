package cn.edu.fudan.flightsys.pojo;

/**
 * Created by junfeng on 12/5/15.
 */
public class Flight {
    private int id;
    private String startPlace;
    /**
     * high bit is hour 0-23,
     * low bit is minute 0-59
     */
    private short startTime;
    private String arrivalPlace;
    private short arrivalTime;
    private Airplane airplane;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartPlace() {
        return startPlace;
    }

    public void setStartPlace(String startPlace) {
        this.startPlace = startPlace;
    }

    public short getStartTime() {
        return startTime;
    }

    public void setStartTime(short startTime) {
        this.startTime = startTime;
    }

    public String getArrivalPlace() {
        return arrivalPlace;
    }

    public void setArrivalPlace(String arrivalPlace) {
        this.arrivalPlace = arrivalPlace;
    }

    public short getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(short arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Airplane getAirplane() {
        return airplane;
    }

    public void setAirplane(Airplane airplane) {
        this.airplane = airplane;
    }

    public static String getPrintableTime(short t) {
        short hour = (short) (t >> 8);
        short minute = (short) (t & 0x00FF);
        return String.format("%d:%d", hour, minute);
    }

    @Override
    public String toString() {
        return "Flight{" +
                "id=" + id +
                ", startPlace='" + startPlace + '\'' +
                ", startTime=" + getPrintableTime(startTime) +
                ", arrivalPlace='" + arrivalPlace + '\'' +
                ", arrivalTime=" + getPrintableTime(arrivalTime) +
                ", airplane=" + airplane +
                '}';
    }
}
