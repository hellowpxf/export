package com.dao;

import java.util.Date;

/**
 * @description:Vacation
 * @author:pxf
 * @data:2023/11/20
 **/
public class Vacation {
    private int id;
    private int employeeId;
    private String leaveType;
    private Date startDate;
    private Date endDate;
    private int totalHours;
    private int usedHours;
    private int remainingHours;
    private Date expiredDate;
    private String notes;
    String gf;
    int ddd;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }

    public int getUsedHours() {
        return usedHours;
    }

    public void setUsedHours(int usedHours) {
        this.usedHours = usedHours;
    }

    public int getRemainingHours() {
        return remainingHours;
    }

    public void setRemainingHours(int remainingHours) {
        this.remainingHours = remainingHours;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Vacation{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", leaveType='" + leaveType + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", totalHours=" + totalHours +
                ", usedHours=" + usedHours +
                ", remainingHours=" + remainingHours +
                ", expiredDate=" + expiredDate +
                ", notes='" + notes + '\'' +
                '}';
    }
}