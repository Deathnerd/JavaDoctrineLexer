package com.deathnerd.dsl;

/**
 * Created by Wes Gilleland on 1/6/2017.
 */
public class Utils {

    public static boolean isNumeric(String value){
        try{
            int t = Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
