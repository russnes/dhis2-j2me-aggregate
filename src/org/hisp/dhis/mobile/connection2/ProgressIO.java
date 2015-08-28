package org.hisp.dhis.mobile.connection2;

/**
 * 
 * @author Paul Mark Castillo
 *
 */
public class ProgressIO
{
    /**
     * 
     */
    protected ProgressListener listener;

    /**
     * 
     */
    private long readBytes;

    /**
     * 
     */
    private long totalBytes;

    /**
     * 
     */
    private boolean realtimeUpdate = true;

    /**
     * 
     * @return
     */
    public ProgressListener getListener()
    {
        return listener;
    }

    /**
     * 
     * @param listener
     */
    public void setListener( ProgressListener listener )
    {
        this.listener = listener;
    }

    /**
     * 
     * @return
     */
    public long getReadBytes()
    {
        return readBytes;
    }

    /**
     * 
     * @param readBytes
     */
    public void setReadBytes( long readBytes )
    {
        if ( listener != null )
        {
            listener.progressUpdated();
        }
        this.readBytes = readBytes;
    }

    /**
     * 
     * @return
     */
    public long getTotalBytes()
    {
        return totalBytes;
    }

    /**
     * 
     * @param totalBytes
     */
    public void setTotalBytes( long totalBytes )
    {
        if ( listener != null )
        {
            listener.progressUpdated();
        }
        this.totalBytes = totalBytes;
    }

    /**
     * 
     * @return
     */
    public boolean isRealtimeUpdate()
    {
        return realtimeUpdate;
    }

    /**
     * 
     * @param realtimeUpdate
     */
    public void setRealtimeUpdate( boolean realtimeUpdate )
    {
        this.realtimeUpdate = realtimeUpdate;
    }
}
