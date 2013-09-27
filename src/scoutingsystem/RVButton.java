/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoutingsystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextField;

/**
 *
 * @author John
 */
public class RVButton extends JButton implements ActionListener{
    JTextField myText;
    Object deltaValue;
    public RVButton()
    {
        super();
        action();
    }
    public RVButton(JTextField myText, Object deltaValue)
    {
        super();
        action();
        this.myText = myText;
        this.deltaValue = deltaValue;
    }
    public final void action()
    {
        setActionCommand("GO!");
        addActionListener(this);
    }
    public void setField(JTextField myText)
    {
        this.myText = myText;
    }
    public void setDelta(double deltaValue)
    {
        this.deltaValue = deltaValue;
    }
    public void setDelta(int deltaValue)
    {
        this.deltaValue = deltaValue;
    }
    public void setDelta(String deltaValue)
    {
        this.deltaValue = deltaValue;
    }
    public void setDelta(Object deltaValue)
    {
        this.deltaValue = deltaValue;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (deltaValue.getClass()==Integer.class)
        {
            String toChange = myText.getText();
            try 
            {
                int value = Integer.parseInt(toChange);
                value += (Integer)(deltaValue);
                myText.setText(""+value);
            }
            catch (Exception ex)
            {
            }
        }
        if (deltaValue.getClass()==Double.class)
        {
            String toChange = myText.getText();
            try 
            {
                double value = Double.parseDouble(toChange);
                value += (Double)(deltaValue);
                myText.setText(""+value);
            }
            catch (Exception ex)
            {
            }
        }
        if (deltaValue.getClass()==String.class)
        {
            String toChange = myText.getText();
            toChange += deltaValue;
            myText.setText(toChange);
        }
        myText.getInputVerifier().verify(myText);
    }
}