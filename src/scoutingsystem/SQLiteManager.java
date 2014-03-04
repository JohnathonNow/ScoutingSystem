/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoutingsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import static scoutingsystem.Networker.hm;

/**
 *
 * @author John
 */
public class SQLiteManager {
    public static void addData(HashMap data)
    {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stat = conn.createStatement();
            String datas = "";
            int d = DataScheme.class.getFields().length;
            for (int i = 0; i < d; i++)
            {
                datas += "\""+data.get(i).toString().replaceAll("\"", "ʺ")+"\"";
                if (i<d-1)
                {
                    datas += ",";
                }
            }
            String string = "INSERT INTO SCOUTING VALUES (" + datas +");";
            System.out.println(string);
            ResultSet rs = stat.executeQuery(string);
        } catch (Exception ex) {
            
        }
    }
    public static HashMap chooseData(int team)
    {
        HashMap hm = new HashMap();
        try {
          //  System.out.println("YES");
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stat = conn.createStatement();
            String datas = "";
            int d = DataScheme.class.getFields().length;
            String string = "select * from SCOUTING where team='"+team+"\'";
           // System.out.println(string);
            ResultSet rs = stat.executeQuery(string);
             
            for (int i = 0; i < DataScheme.class.getFields().length; i++)
            {
                String s = DataScheme.class.getFields()[i].getName();
            //    System.out.println(s);
                 switch ((Integer)Networker.hm.get(DataScheme.class.getFields()[i].getType()))
                 {
                     case 0://int
                         hm.put(s,0);
                     break;
                     case 1://double
                         hm.put(s, 0d);
                     break;
                     case 2://String
                        hm.put(s, "");
                     break;
                     case 3://boolean
                        hm.put(s, 0);
                     break;
                     case 4://long
                         
                         hm.put(s, 0l);
                     break;
                 }
            }
          //  System.out.println("YAY");
        //    System.out.println(hm.get("teleopHighHits"));
            boolean allowed = true;
            int matches = 0;
            
            while (allowed)
            {
                matches += 1;
            for (int i = 0; i < DataScheme.class.getFields().length; i++)
            {
                
                String s = DataScheme.class.getFields()[i].getName();
                 switch ((Integer)Networker.hm.get(DataScheme.class.getFields()[i].getType()))
                 {
                     case 0://int
                         hm.put(s, (Integer)hm.get(s)+rs.getInt(s));
                     break;
                     case 1://double
                         hm.put(s, (Double)hm.get(s)+rs.getDouble(s));
                     break;
                     case 2://String
                        hm.put(s, hm.get(s)+rs.getString(s));
                     break;
                     case 3://boolean
                        hm.put(s, (Integer)hm.get(s)+rs.getInt(s));
                     break;
                     case 4://long
                         hm.put(s, (Long)hm.get(s)+rs.getLong(s));
                     break;
                 }
            }
            allowed = rs.next();
            }
            hm.put("matches", matches);
//            for (int i = 0; i < DataScheme.class.getFields().length; i++)
//            {
//                
//                String s = DataScheme.class.getFields()[i].getName();
//                 switch ((Integer)Networker.hm.get(DataScheme.class.getFields()[i].getType()))
//                 {
//                     case 0://int
//                         hm.put(s, (Double)hm.get(s)/matches);
//                     break;
//                     case 1://double
//                         hm.put(s, (Double)hm.get(s)/matches);
//                     break;
//                     case 2://String
//                        hm.put(s, hm.get(s));
//                     break;
//                     case 3://boolean
//                        hm.put(s, (Double)hm.get(s)/matches);
//                     break;
//                     case 4://long
//                         hm.put(s, (Double)hm.get(s)/matches);
//                     break;
//                 }
//            }
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        //System.err.println(hm.get("autonHighHotHits"));
        return hm;
    }
    public static void construct()
    {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stat = conn.createStatement();
            String datas = "";
            int d = DataScheme.class.getFields().length;
            for (int i = 0; i < d; i++)
            {
                datas += DataScheme.class.getFields()[i].getName();
                if (i<d-1)
                {
                    datas += ",";
                }
            }
            ResultSet rs = stat.executeQuery("CREATE TABLE IF NOT EXISTS SCOUTING (" + datas +");");
        } catch (Exception ex) {
            
        }
    }
}
