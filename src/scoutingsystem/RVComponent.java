/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scoutingsystem;

/**
 *
 * @author John
 */
public interface RVComponent {
    public void reset();
    public void setResetAble(boolean resetAble);
    public boolean getResetAble();
    public void setStartingValue(Object startingValue);
    public Object getStartingValue();
}
