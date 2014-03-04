/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoutingsystem;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 *
 * @author John
 */
public class ScoutFrameClient extends javax.swing.JFrame {
ByteBuffer dataBuffer;
Socket connection;
int myCash = 1000, alwaysBetOnBlack = 0;
boolean success = false;
boolean bluetooth = true;
BTClientManager btcm;
boolean betOnBlue = false, betOnRed = false;
    /**
     * Creates new form NewJFrame
     */
BufferedWriter outputToFile;
    public ScoutFrameClient() {
        try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception e){}
        initComponents();
        this.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent we) {}

            @Override
            public void windowClosing(WindowEvent we) {
                            if (outputToFile!=null) {try {
                        outputToFile.flush();
                         outputToFile.close();
                    } catch (IOException ex) {
                      
                    }
                }
                System.exit(0);}

            @Override
            public void windowClosed(WindowEvent we) {            }

            @Override
            public void windowIconified(WindowEvent we) {}

            @Override
            public void windowDeiconified(WindowEvent we) {}

            @Override
            public void windowActivated(WindowEvent we) {}

            @Override
            public void windowDeactivated(WindowEvent we) {}
        });
        rVSlider1.setToolTipText("<html><u>How efficient are they?</u><br><b>0</b> - They loaf around<br><b>1</b> - They do well<br><b>2</b> - They are amazing!<html>");
        this.defenceRating.setToolTipText("<html><u>How defensive are they?</u><br><b>0</b> - They did nothing<br><b>1</b> - They slowed down a team<br><b>2</b> - They stopped an alliance!<html>");
        this.passRating.setToolTipText("<html><u>How well do they pass?</u><br><b>0</b> - They are slow/inaccurate<br><b>1</b> - They are ok<br><b>2</b> - They are amazing!<html>");
        
        try {
        outputToFile = new BufferedWriter(new FileWriter("sqlBackup.txt", true));
    } catch (IOException ex) {
    }
        myBet.setInputVerifier(new InputVerifier() {

            @Override
            public boolean verify(JComponent jc) {
                int b;
                try
                {
                    b = Integer.parseInt(((JTextField)jc).getText());
                }
                catch (Exception e)
                {
                    return false;
                }
                if (b>myCash)
                {
                    b = myCash;
                }
                alwaysBetOnBlack = b;
                return true;
            }
        });
        if (bluetooth)
        {
            btcm = new BTClientManager(swagMaster);
            btcm.connect();
        }
    }
    private void printQuery()
    {
        try
        {
        String datas = "";
            int d = DataScheme.class.getFields().length;
            for (int i = 0; i < d; i++)
            {
                datas += "\""+DataScheme.getField(i).get(null).toString().replaceAll("\"", "ʺ")+"\"";
                if (i<d-1)
                {
                    datas += ",";
                }
            }
            String string = "INSERT INTO SCOUTING VALUES (" + datas +");";
            if (outputToFile!=null)
            {
                outputToFile.newLine();
                outputToFile.append(string);
                outputToFile.flush();
            }
            System.out.println(string);
        }
        catch (Exception e)
        {
            System.err.println("NO!");
        }
    }
    int fails = 0;
    public void sendDataBT()
    {
        
        dataBuffer = Networker.pack();
        printQuery();
    try {
        fails = 0;
        while (!btcm.connected)
        {
            Thread.sleep(100);
            if (fails++>100)
            {
                JOptionPane.showMessageDialog(null, "Not connected!");
                btcm.close();
                btcm.connect();
                return;
            }
        }
        Networker.transmit(btcm.output, dataBuffer);
        Thread superviser = new Thread(new Runnable()
        {
           @Override
           public void run()
           {   try
               {
                    success = false;
                    boolean redWon = btcm.dis.readBoolean();
                    boolean blueWon = btcm.dis.readBoolean();
                    if (betOnRed && redWon)
                    {
                        myCash += alwaysBetOnBlack*2;
                    }
                    if (betOnBlue && blueWon)
                    {
                        myCash += alwaysBetOnBlack*2;
                    }
                    success = true;
                    johnnyCash.setText("Cash: $"+myCash);
                    betOnRed = false;
                    betOnBlue = false;
                    jTabbedPane1.setSelectedIndex(0);
                    
               }
               catch (IOException e)
               {
                   btcm.connected = false;
                   success = false;
               }
               
           }
        });
        superviser.start();
        long timeEnd = System.currentTimeMillis()+5000;
        while ((System.currentTimeMillis()<timeEnd)&&(!success))
        {
            Thread.sleep(10);
        }
        if (!success)
        {
            superviser.interrupt();
            try
            {
                btcm.close();
            }
            catch (Exception ef)
            {
                
            }
            btcm.connect();
            
        }
        else
        {
            reset();
        }
        //btcm.close();
    } catch (Exception ex) {
    }
    }
    public void sendDataTCP()
    {
        dataBuffer = Networker.pack();
    try {
        printQuery();
        connection = new Socket(ipField.getText(),26071);
        final DataInputStream feedback = new DataInputStream(connection.getInputStream());
        Networker.transmit(connection.getOutputStream(), dataBuffer);
        boolean redWon, blueWon;
        redWon = feedback.readBoolean();
        blueWon = feedback.readBoolean();
        connection.close();
        if (betOnRed && redWon)
        {
            myCash += alwaysBetOnBlack*2;
        }
        if (betOnBlue && blueWon)
        {
            myCash += alwaysBetOnBlack*2;
        }
        johnnyCash.setText("Cash: $"+myCash);
        betOnRed = false;
        betOnBlue = false;
        jTabbedPane1.setSelectedIndex(0);
        reset();
    } catch (Exception e){JOptionPane.showMessageDialog(null, "Not connected!");}
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton2 = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        rVComboBox1 = new scoutingsystem.RVComboBox();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        johnnyCash = new javax.swing.JLabel();
        betBlue = new javax.swing.JButton();
        betRed = new javax.swing.JButton();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        myBet = new javax.swing.JTextField();
        javax.swing.JLabel jLabel8 = new javax.swing.JLabel();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        rVCheckBox1 = new scoutingsystem.RVCheckBox();
        javax.swing.JLabel jLabel9 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel10 = new javax.swing.JLabel();
        autonHighHotHits = new scoutingsystem.RVTextField();
        autonHighMiss = new scoutingsystem.RVTextField();
        autonHighColdHits = new scoutingsystem.RVTextField();
        rVButton1 = new scoutingsystem.RVButton();
        rVButton2 = new scoutingsystem.RVButton();
        rVButton3 = new scoutingsystem.RVButton();
        rVButton4 = new scoutingsystem.RVButton();
        rVButton5 = new scoutingsystem.RVButton();
        rVButton6 = new scoutingsystem.RVButton();
        autonLowColdHits = new scoutingsystem.RVTextField();
        autonLowMiss = new scoutingsystem.RVTextField();
        autonLowHotHits = new scoutingsystem.RVTextField();
        javax.swing.JLabel jLabel11 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel12 = new javax.swing.JLabel();
        rVCheckBox2 = new scoutingsystem.RVCheckBox();
        javax.swing.JLabel jLabel13 = new javax.swing.JLabel();
        blocks = new scoutingsystem.RVTextField();
        rVButton7 = new scoutingsystem.RVButton();
        rVButton8 = new scoutingsystem.RVButton();
        teleopHighHits = new scoutingsystem.RVTextField();
        teleopHighMiss = new scoutingsystem.RVTextField();
        rVButton9 = new scoutingsystem.RVButton();
        javax.swing.JLabel jLabel14 = new javax.swing.JLabel();
        rVButton10 = new scoutingsystem.RVButton();
        teleopLowHits = new scoutingsystem.RVTextField();
        teleopLowMiss = new scoutingsystem.RVTextField();
        rVButton11 = new scoutingsystem.RVButton();
        javax.swing.JLabel jLabel15 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel16 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel17 = new javax.swing.JLabel();
        trussPass = new scoutingsystem.RVTextField();
        rVButton12 = new scoutingsystem.RVButton();
        javax.swing.JLabel jLabel18 = new javax.swing.JLabel();
        rVSlider1 = new scoutingsystem.RVSlider();
        javax.swing.JLabel jLabel19 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        rVTextArea1 = new scoutingsystem.RVTextArea();
        javax.swing.JLabel jLabel20 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel21 = new javax.swing.JLabel();
        timerField = new scoutingsystem.RVTextField();
        rVTimerButton1 = new scoutingsystem.RVTimerButton();
        passRating = new scoutingsystem.RVSlider();
        javax.swing.JLabel jLabel22 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel23 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        rVTextArea2 = new scoutingsystem.RVTextArea();
        minorFoul = new scoutingsystem.RVTextField();
        rVButton13 = new scoutingsystem.RVButton();
        majorFoul = new scoutingsystem.RVTextField();
        rVButton14 = new scoutingsystem.RVButton();
        javax.swing.JLabel jLabel24 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel25 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel26 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel27 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel28 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel29 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel31 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel32 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel33 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel34 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel35 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel36 = new javax.swing.JLabel();
        rVButton15 = new scoutingsystem.RVButton();
        catches = new scoutingsystem.RVTextField();
        javax.swing.JLabel jLabel37 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel38 = new javax.swing.JLabel();
        defenceRating = new scoutingsystem.RVSlider();
        rVCheckBox3 = new scoutingsystem.RVCheckBox();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        ipField = new javax.swing.JTextField();
        rVTextField4 = new scoutingsystem.RVTextField();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        rVTextField2 = new scoutingsystem.RVTextField();
        swagMaster = new javax.swing.JLabel();

        jButton2.setText("jButton2");

        jLabel30.setText("Not:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        rVComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Blue 1", "Blue 2", "Blue 3", "Red 1", "Red 2", "Red 3" }));
        rVComboBox1.setEnabled(false);
        rVComboBox1.setField("scoutNumber");
        rVComboBox1.setResetAble(false);
        rVComboBox1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                rVComboBox1MouseReleased(evt);
            }
        });
        rVComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rVComboBox1ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel4.setText("Greetings, Scout!");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 153, 0));
        jLabel5.setText("Please, place your bet!");

        johnnyCash.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        johnnyCash.setForeground(new java.awt.Color(153, 153, 0));
        johnnyCash.setText("Cash: $1,000");

        betBlue.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        betBlue.setForeground(new java.awt.Color(0, 51, 255));
        betBlue.setText("BLUE ALLIANCE!");
        betBlue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                betBlueActionPerformed(evt);
            }
        });

        betRed.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        betRed.setForeground(new java.awt.Color(255, 0, 0));
        betRed.setText("RED ALLIANCE!");
        betRed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                betRedActionPerformed(evt);
            }
        });

        jLabel7.setText("I would like to bet...");
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        myBet.setText("0");

        jLabel8.setText("dollars on the...");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel4))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(170, 170, 170)
                        .addComponent(johnnyCash)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(241, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(betRed, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(betBlue)
                        .addGap(208, 208, 208))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(42, 42, 42))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(231, 231, 231)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(myBet, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel4)
                .addGap(69, 69, 69)
                .addComponent(jLabel5)
                .addGap(84, 84, 84)
                .addComponent(johnnyCash)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(myBet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(betBlue)
                    .addComponent(betRed, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(104, 104, 104))
        );

        jTabbedPane1.addTab("Gamble", jPanel1);

        jPanel2.setLayout(null);

        jButton1.setText("SUBMIT");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1);
        jButton1.setBounds(715, 13, 77, 25);

        rVCheckBox1.setText("Es Broken!");
        rVCheckBox1.setToolTipText("Did they break?");
        rVCheckBox1.setField("esBroken");
        rVCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rVCheckBox1ActionPerformed(evt);
            }
        });
        jPanel2.add(rVCheckBox1);
        rVCheckBox1.setBounds(610, 40, 90, 25);

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setText("Auton:");
        jPanel2.add(jLabel9);
        jLabel9.setBounds(150, 17, 53, 22);

        jLabel10.setText("High Goal:");
        jPanel2.add(jLabel10);
        jLabel10.setBounds(12, 72, 59, 16);

        autonHighHotHits.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        autonHighHotHits.setText("0");
        autonHighHotHits.setField("autonHighHotHits");
        jPanel2.add(autonHighHotHits);
        autonHighHotHits.setBounds(97, 69, 38, 22);

        autonHighMiss.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        autonHighMiss.setText("0");
        autonHighMiss.setField("autonHighMiss");
        jPanel2.add(autonHighMiss);
        autonHighMiss.setBounds(209, 69, 39, 22);

        autonHighColdHits.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        autonHighColdHits.setText("0");
        autonHighColdHits.setField("autonHighColdHits");
        jPanel2.add(autonHighColdHits);
        autonHighColdHits.setBounds(153, 69, 38, 22);

        rVButton1.setText("+");
        rVButton1.setToolTipText("Did they scored in the high hot goal?");
        rVButton1.setDelta(1);
        rVButton1.setField(autonHighHotHits);
        rVButton1.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jPanel2.add(rVButton1);
        rVButton1.setBounds(96, 104, 39, 35);

        rVButton2.setText("+");
        rVButton2.setToolTipText("Did they score in the high goal while it was not hot?");
        rVButton2.setDelta(1);
        rVButton2.setField(autonHighColdHits);
        rVButton2.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jPanel2.add(rVButton2);
        rVButton2.setBounds(153, 104, 39, 35);

        rVButton3.setText("+");
        rVButton3.setToolTipText("Did they miss a high goal shot?");
        rVButton3.setDelta(1);
        rVButton3.setField(autonHighMiss);
        rVButton3.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jPanel2.add(rVButton3);
        rVButton3.setBounds(210, 104, 39, 35);

        rVButton4.setText("+");
        rVButton4.setToolTipText("Did they score in the not-hot low goal?");
        rVButton4.setDelta(1);
        rVButton4.setField(autonLowColdHits);
        rVButton4.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jPanel2.add(rVButton4);
        rVButton4.setBounds(150, 200, 39, 35);

        rVButton5.setText("+");
        rVButton5.setToolTipText("<html>Did they <b>miss</b> the low goal?</html>");
        rVButton5.setDelta(1);
        rVButton5.setField(autonLowMiss);
        rVButton5.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jPanel2.add(rVButton5);
        rVButton5.setBounds(210, 200, 39, 35);

        rVButton6.setText("+");
        rVButton6.setToolTipText("Did they score in the hot low goal?");
        rVButton6.setDelta(1);
        rVButton6.setField(autonLowHotHits);
        rVButton6.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jPanel2.add(rVButton6);
        rVButton6.setBounds(100, 200, 39, 35);

        autonLowColdHits.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        autonLowColdHits.setText("0");
        autonLowColdHits.setField("autonLowColdHits");
        jPanel2.add(autonLowColdHits);
        autonLowColdHits.setBounds(154, 166, 38, 22);

        autonLowMiss.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        autonLowMiss.setText("0");
        autonLowMiss.setField("autonLowMiss");
        jPanel2.add(autonLowMiss);
        autonLowMiss.setBounds(210, 166, 39, 22);

        autonLowHotHits.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        autonLowHotHits.setText("0");
        autonLowHotHits.setField("autonLowHotHits");
        jPanel2.add(autonLowHotHits);
        autonLowHotHits.setBounds(98, 166, 38, 22);

        jLabel11.setText("Low Goal:");
        jPanel2.add(jLabel11);
        jLabel11.setBounds(14, 169, 57, 16);

        jLabel12.setText("Defense:");
        jPanel2.add(jLabel12);
        jLabel12.setBounds(20, 284, 51, 16);

        rVCheckBox2.setText("Did they start in the goalie zone?");
        rVCheckBox2.setToolTipText("Were they on their opponent's side of the field?");
        rVCheckBox2.setField("inGoalieZone");
        jPanel2.add(rVCheckBox2);
        rVCheckBox2.setBounds(0, 318, 215, 25);

        jLabel13.setText("Blocks:");
        jPanel2.add(jLabel13);
        jLabel13.setBounds(46, 438, 40, 16);

        blocks.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        blocks.setText("0");
        blocks.setField("autonBlockHits");
        jPanel2.add(blocks);
        blocks.setBounds(104, 435, 38, 22);

        rVButton7.setText("+");
        rVButton7.setToolTipText("<html>Did they block a shot in <b>auton</b>?</html>");
        rVButton7.setDelta(1);
        rVButton7.setField(blocks);
        rVButton7.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jPanel2.add(rVButton7);
        rVButton7.setBounds(149, 431, 39, 35);

        rVButton8.setText("+");
        rVButton8.setToolTipText("Did they score in the high goal?");
        rVButton8.setDelta(1);
        rVButton8.setField(teleopHighHits);
        rVButton8.setFocusCycleRoot(true);
        rVButton8.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jPanel2.add(rVButton8);
        rVButton8.setBounds(400, 100, 39, 35);

        teleopHighHits.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        teleopHighHits.setText("0");
        teleopHighHits.setField("teleopHighHits");
        jPanel2.add(teleopHighHits);
        teleopHighHits.setBounds(400, 70, 38, 22);

        teleopHighMiss.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        teleopHighMiss.setText("0");
        teleopHighMiss.setField("teleopHighMiss");
        jPanel2.add(teleopHighMiss);
        teleopHighMiss.setBounds(460, 70, 38, 22);

        rVButton9.setText("+");
        rVButton9.setToolTipText("Did they miss a high goal shot attempt?");
        rVButton9.setDelta(1);
        rVButton9.setField(teleopHighMiss);
        rVButton9.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        rVButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rVButton9ActionPerformed(evt);
            }
        });
        jPanel2.add(rVButton9);
        rVButton9.setBounds(460, 100, 38, 35);

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setText("Teleop:");
        jPanel2.add(jLabel14);
        jLabel14.setBounds(415, 13, 59, 22);

        rVButton10.setText("+");
        rVButton10.setToolTipText("Did they score in the low goal?");
        rVButton10.setDelta(1);
        rVButton10.setField(teleopLowHits);
        rVButton10.setFocusCycleRoot(true);
        rVButton10.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jPanel2.add(rVButton10);
        rVButton10.setBounds(400, 200, 39, 35);

        teleopLowHits.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        teleopLowHits.setText("0");
        teleopLowHits.setField("teleopLowHits");
        jPanel2.add(teleopLowHits);
        teleopLowHits.setBounds(400, 170, 38, 22);

        teleopLowMiss.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        teleopLowMiss.setText("0");
        teleopLowMiss.setField("teleopLowMiss");
        jPanel2.add(teleopLowMiss);
        teleopLowMiss.setBounds(460, 170, 38, 22);

        rVButton11.setText("+");
        rVButton11.setToolTipText("Did they miss the low goal?");
        rVButton11.setDelta(1);
        rVButton11.setField(teleopLowMiss);
        rVButton11.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jPanel2.add(rVButton11);
        rVButton11.setBounds(460, 200, 39, 35);

        jLabel15.setText("High Goal:");
        jPanel2.add(jLabel15);
        jLabel15.setBounds(299, 70, 80, 16);

        jLabel16.setText("Low Goal:");
        jPanel2.add(jLabel16);
        jLabel16.setBounds(307, 170, 80, 16);

        jLabel17.setText("Truss:");
        jPanel2.add(jLabel17);
        jLabel17.setBounds(327, 260, 60, 16);

        trussPass.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        trussPass.setText("0");
        trussPass.setField("trussPassHits");
        jPanel2.add(trussPass);
        trussPass.setBounds(400, 250, 38, 30);

        rVButton12.setText("+");
        rVButton12.setToolTipText("<html>Did they <b>launch</b> the ball over the truss?</html>");
        rVButton12.setDelta(1);
        rVButton12.setField(trussPass);
        rVButton12.setFocusCycleRoot(true);
        rVButton12.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jPanel2.add(rVButton12);
        rVButton12.setBounds(460, 250, 39, 35);

        jLabel18.setText("Defense:");
        jPanel2.add(jLabel18);
        jLabel18.setBounds(430, 350, 51, 16);

        rVSlider1.setMajorTickSpacing(1);
        rVSlider1.setMaximum(2);
        rVSlider1.setMinorTickSpacing(1);
        rVSlider1.setPaintLabels(true);
        rVSlider1.setPaintTicks(true);
        rVSlider1.setSnapToTicks(true);
        rVSlider1.setValue(0);
        rVSlider1.setField("efficiency");
        jPanel2.add(rVSlider1);
        rVSlider1.setBounds(630, 200, 87, 52);

        jLabel19.setText("Rating:");
        jPanel2.add(jLabel19);
        jLabel19.setBounds(350, 400, 41, 16);

        rVTextArea1.setColumns(8);
        rVTextArea1.setRows(4);
        rVTextArea1.setTabSize(5);
        rVTextArea1.setToolTipText("Describe their defense.");
        rVTextArea1.setField("defenceNotes");
        rVTextArea1.setStartingValue("");
        jScrollPane1.setViewportView(rVTextArea1);

        jPanel2.add(jScrollPane1);
        jScrollPane1.setBounds(370, 440, 141, 78);

        jLabel20.setText("Defense Notes:");
        jPanel2.add(jLabel20);
        jLabel20.setBounds(280, 470, 87, 16);

        jLabel21.setText("Human Player Getting:");
        jPanel2.add(jLabel21);
        jLabel21.setBounds(621, 72, 128, 16);

        timerField.setText("0:00");
        jPanel2.add(timerField);
        timerField.setBounds(631, 98, 91, 22);

        rVTimerButton1.setText("Start/Stop");
        rVTimerButton1.setToolTipText("Start when the ball enters the field and stop when they have it.");
        rVTimerButton1.setDisplay(timerField);
        rVTimerButton1.setField("humanPlayerTimer");
        jPanel2.add(rVTimerButton1);
        rVTimerButton1.setBounds(631, 127, 91, 25);

        passRating.setMajorTickSpacing(1);
        passRating.setMaximum(2);
        passRating.setMinorTickSpacing(1);
        passRating.setPaintLabels(true);
        passRating.setPaintTicks(true);
        passRating.setSnapToTicks(true);
        passRating.setValue(0);
        passRating.setField("passRating");
        jPanel2.add(passRating);
        passRating.setBounds(630, 270, 87, 60);

        jLabel22.setText("Robot Goodness:");
        jPanel2.add(jLabel22);
        jLabel22.setBounds(620, 170, 97, 16);

        jLabel23.setText("Notes:");
        jPanel2.add(jLabel23);
        jLabel23.setBounds(580, 470, 37, 16);

        rVTextArea2.setColumns(8);
        rVTextArea2.setRows(4);
        rVTextArea2.setTabSize(5);
        rVTextArea2.setToolTipText("Write anything interesting!");
        rVTextArea2.setField("generalNotes");
        rVTextArea2.setStartingValue("");
        jScrollPane2.setViewportView(rVTextArea2);

        jPanel2.add(jScrollPane2);
        jScrollPane2.setBounds(620, 440, 141, 78);

        minorFoul.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        minorFoul.setText("0");
        minorFoul.setField("minorFouls");
        jPanel2.add(minorFoul);
        minorFoul.setBounds(630, 340, 38, 35);

        rVButton13.setText("+");
        rVButton13.setToolTipText("Did they get any 20 point fouls?");
        rVButton13.setDelta(1);
        rVButton13.setField(minorFoul);
        rVButton13.setFocusCycleRoot(true);
        rVButton13.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jPanel2.add(rVButton13);
        rVButton13.setBounds(680, 340, 39, 35);

        majorFoul.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        majorFoul.setText("0");
        majorFoul.setField("majorFouls");
        jPanel2.add(majorFoul);
        majorFoul.setBounds(630, 390, 38, 35);

        rVButton14.setText("+");
        rVButton14.setToolTipText("Did they get any 50 point fouls?");
        rVButton14.setDelta(1);
        rVButton14.setField(majorFoul);
        rVButton14.setFocusCycleRoot(true);
        rVButton14.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jPanel2.add(rVButton14);
        rVButton14.setBounds(680, 390, 39, 35);

        jLabel24.setText("Foul:");
        jPanel2.add(jLabel24);
        jLabel24.setBounds(579, 340, 40, 16);

        jLabel25.setText("Tech Foul:");
        jPanel2.add(jLabel25);
        jLabel25.setBounds(551, 390, 70, 16);

        jLabel26.setText("Hot:");
        jPanel2.add(jLabel26);
        jLabel26.setBounds(111, 46, 24, 16);

        jLabel27.setText("Hot:");
        jPanel2.add(jLabel27);
        jLabel27.setBounds(111, 143, 24, 16);

        jLabel28.setText("Cold:");
        jPanel2.add(jLabel28);
        jLabel28.setBounds(161, 46, 30, 16);

        jLabel29.setText("Cold:");
        jPanel2.add(jLabel29);
        jLabel29.setBounds(160, 140, 40, 16);

        jLabel31.setText("Miss:");
        jPanel2.add(jLabel31);
        jLabel31.setBounds(218, 46, 30, 16);

        jLabel32.setText("Miss:");
        jPanel2.add(jLabel32);
        jLabel32.setBounds(220, 143, 30, 16);

        jLabel33.setText("Miss:");
        jPanel2.add(jLabel33);
        jLabel33.setBounds(461, 46, 30, 16);

        jLabel34.setText("Miss:");
        jPanel2.add(jLabel34);
        jLabel34.setBounds(462, 143, 30, 16);

        jLabel35.setText("Hit:");
        jPanel2.add(jLabel35);
        jLabel35.setBounds(415, 46, 20, 16);

        jLabel36.setText("Hit:");
        jPanel2.add(jLabel36);
        jLabel36.setBounds(415, 143, 20, 16);

        rVButton15.setText("+");
        rVButton15.setToolTipText("Did they catch the ball after it went over the truss?");
        rVButton15.setDelta(1);
        rVButton15.setField(catches);
        rVButton15.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jPanel2.add(rVButton15);
        rVButton15.setBounds(460, 290, 39, 35);

        catches.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        catches.setText("0");
        catches.setField("catches");
        jPanel2.add(catches);
        catches.setBounds(400, 290, 38, 30);

        jLabel37.setText("Catches:");
        jPanel2.add(jLabel37);
        jLabel37.setBounds(320, 290, 80, 30);

        jLabel38.setText("Pass Rating:");
        jPanel2.add(jLabel38);
        jLabel38.setBounds(550, 280, 80, 16);

        defenceRating.setMajorTickSpacing(1);
        defenceRating.setMaximum(2);
        defenceRating.setMinorTickSpacing(1);
        defenceRating.setPaintLabels(true);
        defenceRating.setPaintTicks(true);
        defenceRating.setSnapToTicks(true);
        defenceRating.setValue(0);
        defenceRating.setField("teleopDefence");
        jPanel2.add(defenceRating);
        defenceRating.setBounds(410, 370, 87, 60);

        rVCheckBox3.setText("Absent?");
        rVCheckBox3.setToolTipText("Did they not even show up?");
        rVCheckBox3.setField("absent");
        rVCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rVCheckBox3ActionPerformed(evt);
            }
        });
        jPanel2.add(rVCheckBox3);
        rVCheckBox3.setBounds(611, 13, 73, 25);

        jTabbedPane1.addTab("Scouting", jPanel2);

        jLabel2.setText("Scout:");

        jLabel3.setText("IP:");

        ipField.setText("127.0.0.1");
        ipField.setEnabled(false);
        ipField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ipFieldMousePressed(evt);
            }
        });
        ipField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ipFieldActionPerformed(evt);
            }
        });

        rVTextField4.setText("John");
        rVTextField4.setField("scoutsName");
        rVTextField4.setResetAble(false);

        jLabel6.setText("Scout Name:");

        jLabel1.setText("Team Number:");

        rVTextField2.setText("2607");
        rVTextField2.setField("team");
        rVTextField2.setResetAble(false);

        swagMaster.setText("Version 1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rVComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ipField, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rVTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addGap(5, 5, 5)
                        .addComponent(rVTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(swagMaster)
                        .addGap(47, 47, 47))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rVComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ipField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3)
                        .addComponent(rVTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(3, 3, 3)
                            .addComponent(jLabel1))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rVTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(swagMaster))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (bluetooth) sendDataBT(); else sendDataTCP();
        //JOptionPane.showMessageDialog(null, "SENT");
    }//GEN-LAST:event_jButton1ActionPerformed
    private void reset()
    {
        List<Component> com = getAllComponents(this);
        for (Component c:com)
        {
            if (c instanceof RVComponent)
            {
                RVComponent rvc = (RVComponent)c;
                if (rvc.getResetAble())
                {
                    rvc.reset();
                }
            }
        }
    }
    private void betRedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_betRedActionPerformed
        betOnRed = true;
        betOnBlue = false;
        myCash -= alwaysBetOnBlack;
        johnnyCash.setText("Cash: $"+myCash);
        jTabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_betRedActionPerformed

    private void betBlueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_betBlueActionPerformed
        betOnRed = false;
        betOnBlue = true;
        myCash -= alwaysBetOnBlack;
        johnnyCash.setText("Cash: $"+myCash);
        jTabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_betBlueActionPerformed

    private void rVButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rVButton9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rVButton9ActionPerformed

    int clickCounterScout = 0, clickCounterIP = 0;
    private void rVComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rVComboBox1ActionPerformed
        // TODO add your handling code here:
        rVComboBox1.setEnabled(false);
        clickCounterScout = 0;
    }//GEN-LAST:event_rVComboBox1ActionPerformed

    private void rVComboBox1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rVComboBox1MouseReleased
        // TODO add your handling code here:
        if (clickCounterScout++>3)
        {
            rVComboBox1.setEnabled(true);
        }
        
    }//GEN-LAST:event_rVComboBox1MouseReleased

    private void ipFieldMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ipFieldMousePressed
        // TODO add your handling code here:
        if (clickCounterIP++>3)
        {
            ipField.setEnabled(true);
        }
    }//GEN-LAST:event_ipFieldMousePressed

    private void ipFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ipFieldActionPerformed
        // TODO add your handling code here:
        ipField.setEnabled(false);
        clickCounterIP = 0;
    }//GEN-LAST:event_ipFieldActionPerformed

    private void rVCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rVCheckBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rVCheckBox1ActionPerformed

    private void rVCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rVCheckBox3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rVCheckBox3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ScoutFrameClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ScoutFrameClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ScoutFrameClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ScoutFrameClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        Networker.init();
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ScoutFrameClient scf = new ScoutFrameClient();
                scf.setVisible(true);
                if (args.length>0)
                {
                    if (args[0].toLowerCase().contains("tcp"))
                    {
                        scf.bluetooth = false;
                    }
                    if (args[0].toLowerCase().contains("btc"))
                    {
                        scf.bluetooth = true;
                    }
                }
                if (args.length>1)
                {
                    scf.ipField.setText(args[1]);
                }
                if (args.length>2)
                {
                    scf.ipField.setText(args[1]);
                }
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private scoutingsystem.RVTextField autonHighColdHits;
    private scoutingsystem.RVTextField autonHighHotHits;
    private scoutingsystem.RVTextField autonHighMiss;
    private scoutingsystem.RVTextField autonLowColdHits;
    private scoutingsystem.RVTextField autonLowHotHits;
    private scoutingsystem.RVTextField autonLowMiss;
    private javax.swing.JButton betBlue;
    private javax.swing.JButton betRed;
    private scoutingsystem.RVTextField blocks;
    private scoutingsystem.RVTextField catches;
    private scoutingsystem.RVSlider defenceRating;
    private javax.swing.JTextField ipField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel johnnyCash;
    private scoutingsystem.RVTextField majorFoul;
    private scoutingsystem.RVTextField minorFoul;
    private javax.swing.JTextField myBet;
    private scoutingsystem.RVSlider passRating;
    private scoutingsystem.RVButton rVButton1;
    private scoutingsystem.RVButton rVButton10;
    private scoutingsystem.RVButton rVButton11;
    private scoutingsystem.RVButton rVButton12;
    private scoutingsystem.RVButton rVButton13;
    private scoutingsystem.RVButton rVButton14;
    private scoutingsystem.RVButton rVButton15;
    private scoutingsystem.RVButton rVButton2;
    private scoutingsystem.RVButton rVButton3;
    private scoutingsystem.RVButton rVButton4;
    private scoutingsystem.RVButton rVButton5;
    private scoutingsystem.RVButton rVButton6;
    private scoutingsystem.RVButton rVButton7;
    private scoutingsystem.RVButton rVButton8;
    private scoutingsystem.RVButton rVButton9;
    private scoutingsystem.RVCheckBox rVCheckBox1;
    private scoutingsystem.RVCheckBox rVCheckBox2;
    private scoutingsystem.RVCheckBox rVCheckBox3;
    private scoutingsystem.RVComboBox rVComboBox1;
    private scoutingsystem.RVSlider rVSlider1;
    private scoutingsystem.RVTextArea rVTextArea1;
    private scoutingsystem.RVTextArea rVTextArea2;
    private scoutingsystem.RVTextField rVTextField2;
    private scoutingsystem.RVTextField rVTextField4;
    private scoutingsystem.RVTimerButton rVTimerButton1;
    private javax.swing.JLabel swagMaster;
    private scoutingsystem.RVTextField teleopHighHits;
    private scoutingsystem.RVTextField teleopHighMiss;
    private scoutingsystem.RVTextField teleopLowHits;
    private scoutingsystem.RVTextField teleopLowMiss;
    private scoutingsystem.RVTextField timerField;
    private scoutingsystem.RVTextField trussPass;
    // End of variables declaration//GEN-END:variables
public static List<Component> getAllComponents(final Container c) {
    Component[] comps = c.getComponents();
    List<Component> compList = new ArrayList<Component>();
    for (Component comp : comps) {
        compList.add(comp);
        if (comp instanceof Container)
            compList.addAll(getAllComponents((Container) comp));
    }
    return compList;
}
}
