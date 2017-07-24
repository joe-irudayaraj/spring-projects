package com.jolokia;

import java.io.Serializable;

/**
 * Created by user on 7/21/2017.
 */
public class Notification implements Serializable{
    private static final long serialVersionUID = 1L;

    private String eventType;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getLoanNumber() {
        return loanNumber;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "eventType='" + eventType + '\'' +
                ", loanNumber='" + loanNumber + '\'' +
                '}';
    }

    public void setLoanNumber(String loanNumber) {
        this.loanNumber = loanNumber;
    }

    private String loanNumber;
}
