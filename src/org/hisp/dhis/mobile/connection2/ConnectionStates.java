package org.hisp.dhis.mobile.connection2;

/**
 * 
 * @author Paul Mark Castillo
 * 
 */
public class ConnectionStates
{
    /**
     * 
     */
    public static final int IDLE = 0;

    /**
     * 
     */
    public static final int CONNECTING = 1;

    /**
     * 
     */
    public static final int CONNECTED = 2;

    /**
     * 
     */
    public static final int SENDING = 3;

    /**
     * 
     */
    public static final int SENT = 4;

    /**
     * 
     */
    public static final int RECEIVING = 5;

    /**
     * 
     */
    public static final int RECEIVED = 6;

    /**
     * 
     */
    public static final int DISCONNECTING = 7;

    /**
     * 
     */
    public static final int DISCONNECTED = 8;

    /**
     * 
     */
    public static final int ERROR = 9;

    /**
     * 
     */
    public static final int CANCELED = 10;
}
