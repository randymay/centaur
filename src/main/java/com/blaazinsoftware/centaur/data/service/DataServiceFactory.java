package com.blaazinsoftware.centaur.data.service;

/**
 * @author Randy May
 *         Date: 14-10-31
 */
public class DataServiceFactory {
    public static DataService getInstance() {
        return new DefaultDataServiceImpl();
    }
}
