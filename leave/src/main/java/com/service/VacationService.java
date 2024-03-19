package com.service;

import com.dao.Vacation;

/**
 * @author Administrator
 */
public interface VacationService {
    void insertLeave(Vacation vacation);
    Vacation getLeaveById(int id);
    void updateLeave(Vacation vacation);
    void deleteLeave(int id);
}
