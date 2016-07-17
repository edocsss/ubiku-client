package com.prys.constant;

/**
 * Created by edocsss on 10/7/16.
 */
public class Regex {
    public static final String USER_FULL_NAME = "[A-Za-z]{3,50}";
    public static final String USER_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$"; // >= 8 chars, 1 UC, 1 LC, 1 Number
}
