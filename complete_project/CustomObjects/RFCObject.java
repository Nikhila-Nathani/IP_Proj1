/**
 * Copyright 2019 Hewlett Packard Enterprise Development LP
 */
package CustomObjects;

/**
 * @author rkoneru
 *
 */
import java.io.Serializable;

public class RFCObject implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    Integer number;
    String title;
    String hostname;
    Integer RFCServerPortNumber;
    Integer TTL;

    public RFCObject(Integer number, String title, String hostname, Integer RFCServerPortNumber) {
        this.number = number;
        this.title = title;
        this.hostname = hostname;
        this.TTL = 7200;
        this.RFCServerPortNumber = RFCServerPortNumber;

    }

    @Override
    public String toString() {
        return "RFCObject [number=" + number + ", title=" + title + ", hostname=" + hostname + ", RFCServerPortNumber=" + RFCServerPortNumber + ", TTL=" + TTL + "]";
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getTTL() {
        return TTL;
    }

    public void setTTL(Integer tTL) {
        TTL = tTL;
    }

}
