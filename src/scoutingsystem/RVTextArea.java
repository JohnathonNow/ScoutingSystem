/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoutingsystem;

import java.lang.reflect.Field;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextArea;

/**
 *
 * @author John
 */
public class RVTextArea extends JTextArea implements RVComponent {
    Field myValue;
    public RVTextArea()
    {
        super();
        addVerifier();
    }
    public RVTextArea(Field myValue)
    {
        super();
        this.myValue = myValue;
        addVerifier();
    }
    public final void addVerifier()
    {
        this.setInputVerifier(new InputVerifier() {

            @Override
            public boolean verify(JComponent jc) {
                ((RVTextArea)jc).update();
                return true;
            }
        });
    }
    public void setField(String myValue)
    {
        this.myValue = DataScheme.getField(myValue);
        this.getInputVerifier().verify(this);
    }
    public void setValue(Object value)
    {
        try {
            myValue.set(null, value);
        } catch (Exception ex) {
            
        }
    }

    public void update()
    {
        try
        {
            if (myValue.getType()==Integer.TYPE)
            {
                setValue((int)Double.parseDouble(this.getText()));
            }
            if (myValue.getType()==Double.TYPE)
            {
                setValue(Double.parseDouble(this.getText()));
            }
            if (myValue.getType()==String.class)
            {
                setValue(this.getText());
            }
        }
        catch (Exception e)
        {
            
        }
    }

    @Override
    public void reset() {
        this.setText(startingValue.toString());
        update();
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