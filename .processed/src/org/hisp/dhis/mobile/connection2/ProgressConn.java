package org.hisp.dhis.mobile.connection2;

/**
 * 
 * @author Paul Mark Castillo
 * 
 */
public class ProgressConn
    extends ProgressIO
{
    /**
     * 
     */
    private int state = ConnectionStates.IDLE;

    /**
     * 
     * @return
     */
    public int getState()
    {
        return state;
    }

    /**
     * 
     * @param state
     */
    public void setState( int state )
    {
        this.state = state;
        if ( listener != null )
        {
            listener.progressUpdated();
        }
        else
        {
        }
    }
}
