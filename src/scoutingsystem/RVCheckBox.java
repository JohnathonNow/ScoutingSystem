/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoutingsystem;

import java.lang.reflect.Field;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

/**
 *
 * @author John
 */
public class RVCheckBox extends JCheckBox  implements RVComponent{
    Field myValue;
    public RVCheckBox()
    {
        super();
        addVerifier();
    }
    public RVCheckBox(Field myValue)
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
                ((RVCheckBox)jc).update();
                return true;
            }
        });
    }
    public void setField(String myValue)
    {
        this.myValue = DataScheme.getField(myValue);
        this.getInputVerifier().verify(this);
    }
    public void setValue(boolean value)
    {
        try {
            myValue.set(null, value);
        } catch (Exception ex) {
            
        }
    }

    public void update()
    {
       setValue(this.isSelected());
    }

    @Override
    public void reset() {
        this.setSelected(false);
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