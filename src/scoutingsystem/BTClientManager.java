/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoutingsystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.swing.JLabel;

/**
 *
 * @author John
 */
public class BTClientManager implements DiscoveryListener, Runnable{
    ArrayList<RemoteDevice> devices;
    ArrayList<ServiceRecord> services;
    LocalDevice local;
    DiscoveryAgent agent;
    DataOutputStream dout;
    public DataInputStream  dis;
    int currentDevice = 0;
    boolean connected = false;
    public OutputStream output = null;
    StreamConnection conn = null;
    JLabel updater;
    public BTClientManager(JLabel e)
    {
        updater = e;
        updater.setText("Connecting...");
    }
    public void close()
    {
            try {
                me.interrupt();
                conn.close();
            } catch (IOException ex) {}
                connected = false;
                updater.setText("NOT CONNECTED");
    }
    Thread me;
    public void connect()
    {
        me = new Thread(this);
        me.start();
    }
    public void FindDevices(){
    try{
        devices              = new ArrayList();
        local    = LocalDevice.getLocalDevice();
        agent = local.getDiscoveryAgent();
        System.out.println(agent.toString());
        agent.startInquiry(DiscoveryAgent.GIAC,this);
        
    }catch(Exception e){}
 }
     
public void FindServices(RemoteDevice device){
    try{
        UUID[] uuids  = new UUID[1];
        uuids[0]      = new UUID("2607201426072014260720142607",false);    //The UUID of the ech service
        local         = LocalDevice.getLocalDevice();
        agent         = local.getDiscoveryAgent();
        
        agent.searchServices(null,uuids,device,this);            
    }catch(Exception e){}
}
    
    @Override
    public void deviceDiscovered(RemoteDevice remoteDevice,DeviceClass deviceClass) {
    devices.add(remoteDevice);
}

    @Override
    public void servicesDiscovered(int transID,ServiceRecord[] serviceRecord) {
        services.addAll(Arrays.asList(serviceRecord));
}
    @Override
    public void inquiryCompleted(int param){
    switch (param) {
        case DiscoveryListener.INQUIRY_COMPLETED:    //Inquiry completed normally
            if (devices.size() > 0){                 //Atleast one device has been found
                services = new ArrayList();
                this.FindServices((RemoteDevice)
                         devices.get(0));     //Check if the first device offers the service
            }
             //   do_alert("No device found in range",4000);
        break;
        case DiscoveryListener.INQUIRY_ERROR:       // Error during inquiry
           // this.do_alert("Inqury error" , 4000);
        break;
        case DiscoveryListener.INQUIRY_TERMINATED:  // Inquiry terminated by agent.cancelInquiry()
             //this.do_alert("Inqury Canceled" , 4000);
        break;
       }
}
    
    @Override
    public void serviceSearchCompleted(int transID, int respCode) {
    switch(respCode) {
        case DiscoveryListener.SERVICE_SEARCH_COMPLETED:
            
            if(currentDevice == devices.size() -1){ //all devices have been searched
                if(services.size() <= 0){
                currentDevice++;
                this.FindServices((RemoteDevice)devices.get(currentDevice));
                }
            }
        break;
        case DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
             //this.do_alert("Device not Reachable" , 4000);
        break;
        case DiscoveryListener.SERVICE_SEARCH_ERROR:
             //this.do_alert("Service serch error" , 4000);
            
        break;
        case DiscoveryListener.SERVICE_SEARCH_NO_RECORDS:
            //this.do_alert("No records returned" , 4000);
        break;
        case DiscoveryListener.SERVICE_SEARCH_TERMINATED:
            //this.do_alert("Inqury Cancled" , 4000);
        break;
     }
}

    @Override
    public void run() {
        try {
            connected = false;
    //        FindDevices();
    //        while (devices==null||devices.isEmpty())
    //        {
    //            //Thread.yield();
    //        }
    //        RemoteDevice theRightOne = null;
    //        while (theRightOne == null)
    //        {
    //            for (RemoteDevice d:devices)
    //            {
    //                try {
    //                    if (d.getFriendlyName(false).contains("PC"))
    //                    {
    //                        theRightOne = d;
    //                        break;
    //                    }
    //                } catch (IOException ex) {
    //                    continue;
    //                }
    //            }
    //            //Thread.yield();
    //        }
    //        FindServices(theRightOne);
    //        
    //        while (services==null||services.isEmpty())
    //        {
    //            //Thread.yield();
    //        }
    //        ServiceRecord correctService = services.get(0);
            local    = LocalDevice.getLocalDevice();
            agent = local.getDiscoveryAgent();
            String url;
            
            //url = correctService.getConnectionURL(ServiceRecord.AUTHENTICATE_ENCRYPT,false);

            try {

                url = agent.selectService(new UUID("2607201426072014260720142607",false) , ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                conn = (StreamConnection) Connector.open(url);
                output = conn.openOutputStream();
                dis = conn.openDataInputStream();
                connected = true;
                updater.setText("CONNECTED!");
            } catch (Exception ex) {
                connected = false;
            
            }
        } catch (BluetoothStateException ex) {
            Logger.getLogger(BTClientManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (Exception e)
        {
            connected = false;
        }
        
    }
}
