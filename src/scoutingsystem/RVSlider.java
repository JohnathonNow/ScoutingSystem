/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoutingsystem;

import java.lang.reflect.Field;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JSlider;

/**
 *
 * @author John
 */
public class RVSlider extends JSlider implements RVComponent{
    Field myValue;
    public RVSlider()
    {
        super();
        addVerifier();
    }
    public RVSlider(Field myValue)
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
                ((RVSlider)jc).update();
                return true;
            }
        });
    }
    public void setField(String myValue)
    {
        this.myValue = DataScheme.getField(myValue);
        this.getInputVerifier().verify(this);
    }
    public void setValued(int value)
    {
        try {
            myValue.set(null, value);
        } catch (Exception ex) {
            
        }
    }

    public void update()
    {
       setValued(this.getValue());
    }

    @Override
    public void reset() {
        this.setValue(this.getMinimum());
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