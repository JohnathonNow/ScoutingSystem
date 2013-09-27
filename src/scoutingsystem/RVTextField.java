/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoutingsystem;

import java.lang.reflect.Field;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 *
 * @author John
 */
public class RVTextField extends JTextField {
    Field myValue;
    public RVTextField()
    {
        super();
        addVerifier();
    }
    public RVTextField(Field myValue)
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
                ((RVTextField)jc).update();
                try {
                    System.out.println(((RVTextField)jc).myValue.get(null));
                } catch (Exception ex) {}
                return true;
            }
        });
    }
    public void setField(String myValue)
    {
        this.myValue = DataScheme.getField(myValue);
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
}