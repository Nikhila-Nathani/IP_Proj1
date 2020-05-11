/**
 * Copyright 2019 Hewlett Packard Enterprise Development LP
 */
package CustomObjects;

import java.time.LocalDate;

/**
 * @author rkoneru
 *
 */
public class Peer {
    private String hostname;
    private Boolean isActive = false;
    private final int cookie;

    private int TTL;
    private int port;
    private int activeCount;
    private LocalDate lastRegistrationDate;

    @Override
    public String toString() {
        return "Peer [hostname=" + hostname + ", isActive=" + isActive + ", cookie=" + cookie + ", TTL=" + TTL + ", port=" + port + ", activeCount=" + activeCount + ", lastRegistrationDate=" + lastRegistrationDate + "]";
    }

    public Peer(String hostname, int port, int cookie) {
        this.cookie = cookie;
        this.port = port;
        this.hostname = hostname;
        this.TTL = 7200;
        this.activeCount = 1;
        this.isActive = true;
    }

    public int getCookie() {
        return cookie;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public int getTTL() {
        return TTL;
    }

    public void setTTL(int tTL) {
        TTL = tTL;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(int activeCount) {
        this.activeCount = activeCount;
    }

    public LocalDate getLastRegistrationDate() {
        return lastRegistrationDate;
    }

    public void setLastRegistrationDate(LocalDate lastRegistrationDate) {
        this.lastRegistrationDate = lastRegistrationDate;
    }

}
