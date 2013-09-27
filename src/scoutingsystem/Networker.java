/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoutingsystem;

import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 *
 * @author John
 */
public class Networker {
    static HashMap hm = new HashMap();
    public static void init()
    {
        hm.put(Integer.TYPE, 0);
        hm.put(Double.TYPE, 1);
        hm.put(String.class, 2);
    }
    public static ByteBuffer pack()
    {
        try 
        {
            int numberOfBytes = 0;
            for (int i = 0; i < DataScheme.class.getFields().length; i++)
            {
                 switch ((Integer)hm.get(DataScheme.class.getFields()[i].getType()))
                 {
                     case 0://int
                         numberOfBytes+=8;
                     break;
                     case 1://double
                         numberOfBytes+=12;
                     break;
                     case 2://String
                        String yay = (String) DataScheme.class.getFields()[i].get(null);
                        numberOfBytes+=(yay.length()*2)+8;
                     break;
                 }
            }
            ByteBuffer buffer = ByteBuffer.allocate(numberOfBytes);
            for (int i = 0; i < DataScheme.class.getFields().length; i++)
            {
                 switch ((Integer)hm.get(DataScheme.class.getFields()[i].getType()))
                 {
                     case 0://int
                         buffer.putInt(0);
                         buffer.putInt((Integer)DataScheme.class.getFields()[i].get(null));
                     break;
                     case 1://double
                         buffer.putInt(1);
                         buffer.putDouble((Double)DataScheme.class.getFields()[i].get(null));
                     break;
                     case 2://String
                        try 
                        {
                            buffer.putInt(2);
                            String yay = (String) DataScheme.class.getFields()[i].get(null);
                            buffer.putInt(yay.length());
                            buffer.put(yay.getBytes());
                        } catch (Exception ex) {}
                     break;
                 }
            }
            byte yay[] = new byte[buffer.position()];//toSend.slice().array();
            System.arraycopy(buffer.array(), 0, yay, 0, buffer.position());
            return ByteBuffer.wrap(yay);
        } catch (Exception ex) {ex.printStackTrace();}
                return null;
    }
    public static HashMap unpack(ByteBuffer buf)
    {
        HashMap data = new HashMap();
        int index = 0;
        while (buf.hasRemaining())
        {
            switch (buf.getInt())
            {
                case 0:
                    int valui = buf.getInt();
                    data.put(index, valui);
                    System.out.println(valui);
                break;
                case 1:
                    double valud = buf.getDouble();
                    System.out.println(valud);
                    data.put(index, valud);
                break;
                case 2:
                    int size = buf.getInt();
                    byte[] string = new byte[size];
                    buf.get(string);
                    String valus = new String(string);
                    System.out.println(valus);
                    data.put(index, valus);
                break;
            }
            index++;
        }
        buf.rewind();
        return data;
    }
}