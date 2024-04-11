package com.example.cyfinance.util;
//Having both urls outside of the class helps in
//minimizing writing the URL in each class in
//which it's used
public class Constants {
    public static final String GET_EARNINGS_BY_ID = "/earnings/get";
    public static final String UPDATE_EARNINGS = "/earnings/update";
    public static final String CREATE_EARNINGS = "/earnings/create";
    // url to use for localhost testing: http://10.0.2.2:8080
    // url to use for server testing: http://coms-309-038.class.las.iastate.edu:8080
    public static final String URL = "http://coms-309-038.class.las.iastate.edu:8080";
    public static final String WS = "ws://coms-309-038.class.las.iastate.edu:8080";

}

//This class is basically helpful to have the URL ports that we need to get to
