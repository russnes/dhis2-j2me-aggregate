package org.hisp.dhis.mobile.connection2;

import org.hisp.dhis.mobile.imagereports.ProgressAlertNetwork;

/**
 * 
 * @author Paul Mark Castillo
 * 
 */
public class NetworkRequest
{
    /**
     * 
     */
    private CallbackNetworkResponse callback;

    /**
     * 
     */
    private String completePath;

    /**
     * 
     */
    private String path;

    /**
     * 
     */
    private ProgressAlertNetwork progressAlert;

    /**
     * 
     * @return
     */
    public String getPath()
    {
        return path;
    }

    /**
     * 
     * @param path
     */
    public void setPath( String path )
    {
        this.path = path;
    }

    /**
     * 
     * @return
     */
    public CallbackNetworkResponse getResponseCallback()
    {
        return callback;
    }

    /**
     * 
     * @param listener
     */
    public void setResponseCallback( CallbackNetworkResponse listener )
    {
        this.callback = listener;
    }

    /**
     * 
     * @return
     */
    public ProgressAlertNetwork getProgressAlert()
    {
        return progressAlert;
    }

    /**
     * 
     * @param progressAlert
     */
    public void setProgressAlert( ProgressAlertNetwork progressAlert )
    {
        this.progressAlert = progressAlert;
    }

    /**
     * 
     * @return
     */
    public String getCompletePath()
    {
        return completePath;
    }

    /**
     * 
     * @param completePath
     */
    public void setCompletePath( String completePath )
    {
        this.completePath = completePath;
    }
}
