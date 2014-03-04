/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoutingsystem;

import java.lang.reflect.Field;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;

/**
 *
 * @author John
 */
public class RVComboBox extends JComboBox  implements RVComponent{
    Field myValue;
    public RVComboBox()
    {
        super();
        addVerifier();
    }
    public RVComboBox(Field myValue)
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
                ((RVComboBox)jc).update();
                return true;
            }
        });
    }
    public void setField(String myValue)
    {
        this.myValue = DataScheme.getField(myValue);
        this.getInputVerifier().verify(this);
    }
    public void setValue(int value)
    {
        try {
            myValue.set(null, value);
        } catch (Exception ex) {
            
        }
    }

    public void update()
    {
       setValue(this.getSelectedIndex());
    }
    @Override
    public void reset()
    {
        this.setSelectedIndex(0);
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