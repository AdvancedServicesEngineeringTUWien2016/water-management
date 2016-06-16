package at.ac.tuwien.ase2016.wm.server.web;

import java.time.ZonedDateTime;

public class ErrorModel {

    private ZonedDateTime start;
    private ZonedDateTime end;

    public ErrorModel() {
    }

    public ErrorModel(ZonedDateTime start, ZonedDateTime end) {
        this.start = start;
        this.end = end;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }
}
