/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoutingsystem;

import java.lang.reflect.Field;

/**
 *
 * @author John
 */
public class DataScheme {
    public static int topAutonPoints = 0;
    public static String notes = "";
    public static Field getField(String name)
    {
        try
        {
            return DataScheme.class.getField(name);
        }
        catch (Exception e)
        {
            System.err.println(DataScheme.class.getFields()[1].getName());
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
