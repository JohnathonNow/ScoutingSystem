/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoutingsystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;

/**
 *
 * @author John
 */
public class RVTimer extends JButton implements ActionListener, Runnable, RVComponent{
    Field myValue;
    boolean timer = false;
    long startTime = 0;
    long totalTime = 0;
    public RVTimer()
    {
        super();
        addVerifier();
    }
    public RVTimer(Field myValue)
    {
        super();
        this.myValue = myValue;
        addVerifier();
    }
    public final void addVerifier()
    {
        Thread me = new Thread(this);
        me.start();
        this.setActionCommand("PRESS");
        this.addActionListener(this);
        this.setInputVerifier(new InputVerifier() {

            @Override
            public boolean verify(JComponent jc) {
                
                return true;
            }
        });
    }
    public void setField(String myValue)
    {
        this.myValue = DataScheme.getField(myValue);
        this.getInputVerifier().verify(this);
    }
    public void setValued(long value)
    {
        try {
            myValue.set(null, value);
        } catch (Exception ex) {
            
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        startTime = System.currentTimeMillis();
        timer = !timer;
        setValued(totalTime);
    }

    @Override
    public void run() {
        while (true)
        {
            if (timer)
            {
                totalTime += System.currentTimeMillis()-startTime;
                startTime = System.currentTimeMillis();
                this.setText(""+(totalTime/1000d));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {}
            }
        }
    }

    @Override
    public void reset() {
        totalTime = 0;
        timer = false;
        setValued(0);
    }
    public boolean resetAble = true;
    @Override
    public void setResetAble(boolean resetAble)
    {
        this.resetAble = resetAble;
    }
    @Override
    public boolean getResetAble()
    {
        return this.resetAble;
    }
    Object startingValue = new Integer(0);
    @Override
    public void setStartingValue(Object startingValue)
    {
        this.startingValue = startingValue;
    }
    @Override
    public Object getStartingValue()
    {
        return this.startingValue;
    }
 }