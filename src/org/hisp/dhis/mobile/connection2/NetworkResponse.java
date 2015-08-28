package org.hisp.dhis.mobile.connection2;

import java.util.Hashtable;

/**
 * 
 * @author Paul Mark Castillo
 * 
 */
public class NetworkResponse
{
    /**
     * 
     */
    private int responseCode;

    /**
     * 
     */
    private String responseDescription;

    /**
     * 
     */
    private byte[] content;

    /**
     * 
     */
    private Exception exception;

    /**
     * 
     */
    private int lastStatus;

    /**
     * 
     */
    private Hashtable responseHeaders;

    /**
     * 
     * @return
     */
    public int getResponseCode()
    {
        return responseCode;
    }

    /**
     * 
     * @param responseCode
     */
    public void setResponseCode( int responseCode )
    {
        this.responseCode = responseCode;
    }

    /**
     * 
     * @return
     */
    public String getResponseDescription()
    {
        return responseDescription;
    }

    /**
     * 
     * @param responseDescription
     */
    public void setResponseDescription( String responseDescription )
    {
        this.responseDescription = responseDescription;
    }

    /**
     * 
     * @return
     */
    public byte[] getContent()
    {
        return content;
    }

    /**
     * 
     * @param content
     */
    public void setContent( byte[] content )
    {
        this.content = content;
    }

    /**
     * 
     * @return
     */
    public Exception getException()
    {
        return exception;
    }

    /**
     * 
     * @param exception
     */
    public void setException( Exception exception )
    {
        this.exception = exception;
    }

    /**
     * 
     * @return
     */
    public int getLastStatus()
    {
        return lastStatus;
    }

    /**
     * 
     * @param lastStatus
     */
    public void setLastStatus( int lastStatus )
    {
        this.lastStatus = lastStatus;
    }

    /**
     * 
     * @return
     */
    public Hashtable getResponseHeaders()
    {
        return responseHeaders;
    }

    /**
     * 
     * @param responseHeaders
     */
    public void setResponseHeaders( Hashtable responseHeaders )
    {
        this.responseHeaders = responseHeaders;
    }
}
