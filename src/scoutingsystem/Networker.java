/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoutingsystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 *
 * @author John
 */
public class Networker {
    public static HashMap hm = new HashMap();
    public static void init()
    {
        hm.put(Integer.TYPE, 0);
        hm.put(Double.TYPE, 1);
        hm.put(String.class, 2);
        hm.put(Boolean.TYPE, 3);
        hm.put(Long.TYPE, 4);
    }
    public static ByteBuffer pack()
    {
        try 
        {
            int numberOfBytes = 4;
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
                        numberOfBytes+=(yay.length()*1)+8;
                     break;
                     case 3://boolean
                        numberOfBytes+=8;
                     break;
                     case 4://long
                         numberOfBytes+=16;
                     break;
                 }
            }
            ByteBuffer buffer = ByteBuffer.allocate(numberOfBytes);
            buffer.putInt(0);//Packet indentifier
            for (int i = 0; i < DataScheme.class.getFields().length; i++)
            {
                 switch ((Integer)hm.get(DataScheme.class.getFields()[i].getType()))
                 {
                     case 0://int
                         buffer.putInt(0);
                         buffer.putInt((Integer)DataScheme.class.getFields()[i].get(null));
//                         System.out.println((Integer)DataScheme.class.getFields()[i].get(null));
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
                            //System.out.println(yay+" "+yay.getBytes("UTF-8").length);
                            buffer.put(yay.getBytes());
                        } catch (Exception ex) {}
                     break;
                     case 3://boolean
                         buffer.putInt(3);
                         buffer.putInt((Boolean)DataScheme.class.getFields()[i].get(null)?1:0);
                     break;
                     case 4://long
                         buffer.putInt(4);
                         buffer.putLong((Long)DataScheme.class.getFields()[i].get(null));
                     break;
                 }
            }
            byte yay[] = new byte[buffer.position()];//toSend.slice().array();
            //System.out.println(numberOfBytes + " , " + buffer.position());
            System.arraycopy(buffer.array(), 0, yay, 0, buffer.position());
            buffer = ByteBuffer.wrap(yay);
            unpack(buffer);
            return buffer;
        } catch (Exception ex) {ex.printStackTrace();}
                return null;
    }
    public static HashMap unpack(ByteBuffer buf)
    {
        HashMap data = new HashMap();
        int index = 0;
        buf.rewind();
        buf.getInt();//identifier must be 0
        while (buf.hasRemaining())
        {
            int yay = buf.getInt();
            switch (yay)
            {
                case 0:
                    int valui = buf.getInt();
                    data.put(index, valui);
//                    System.out.println(valui);
                break;
                case 1:
                    double valud = buf.getDouble();
//                    System.out.println(valud);
                    data.put(index, valud);
                break;
                case 2:
                    int size = buf.getInt();
//                    System.out.println(size);
                    byte[] string = new byte[size];
                    buf.get(string);
                    String valus = new String(string);
//                    System.out.println(valus);
                    data.put(index, valus);
                break;
                case 3:
                    int valub = buf.getInt();
                    data.put(index, valub);
//                    System.out.println(valui);
                break;
                case 4:
                    long valuc = buf.getLong();
                    data.put(index, valuc);
//                    System.out.println(valui);
                break;
            }
            index++;
        }
        buf.rewind();
        return data;
    }
    public static void transmit(OutputStream bos, ByteBuffer toSend)
    {
        try
        {
            ByteBuffer size = ByteBuffer.allocate(4).putInt(toSend.limit());
            toSend.rewind();
            size.rewind();
            bos.write(size.array());
            bos.write(toSend.array());
            //bos.flush();
        } catch (IOException ex) {}
    }
    public static ByteBuffer recieve(InputStream bis)
    {
        try
        {
            byte[] bytes = new byte[4];
            bis.read(bytes);
            int size = ByteBuffer.wrap(bytes).getInt();
//            System.out.println(size);
            byte[] construct = new byte[size];
            bis.read(construct);
            return ByteBuffer.wrap(construct);
        } catch (IOException ex) {return null;}
    }
}