/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoutingsystem;

import java.lang.reflect.Field;

/**
 *
 * @author John
 * This class is for highlighting the data scheme for both the sent data
 * as well as the data entered into the database.
 * 
 * Fields are created by declaring a field as public static.
 * The following types are currently supported:
 *      boolean     int     long    double     String
 */
public class DataScheme {
    public static int team = 2607;
    public static String scoutsName = "John";
    public static int scoutNumber = 0;
    
    public static boolean absent = false,   esBroken = false;
    
    public static int autonHighColdHits = 0, autonHighHotHits = 0, autonHighMiss = 0;
    public static int autonLowColdHits = 0, autonLowHotHits = 0, autonLowMiss = 0;
    public static boolean mobility = false;
    
    public static boolean inGoalieZone = false;
    public static int     autonBlockHits = 0;
    
    public static int teleopHighHits    = 0, teleopHighMiss = 0;
    public static int teleopLowHits     = 0, teleopLowMiss  = 0;
    public static int trussPassHits     = 0;
    public static int passRating        = 0;
    public static int catches           = 0;
    
    public static int teleopDefence     = 0;
    public static String defenceNotes   = "";
    
    public static long humanPlayerTimer = 0;
    public static int  efficiency       = 0;
    public static int  minorFouls       = 0, majorFouls = 0;
    
    public static String generalNotes   = "";
    
    public static Field getField(String name)
    {
        try
        {
            return DataScheme.class.getField(name);
        }
        catch (Exception e)
        {
            System.err.print("ACTUALLY BAD THING HAPPENED! Unknown field: ");
            System.err.println(name);
            return null;
        }
    }
        public static Field getField(int number)
    {
        try
        {
            return DataScheme.class.getFields()[number];
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
