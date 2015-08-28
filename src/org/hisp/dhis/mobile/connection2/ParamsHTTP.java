package org.hisp.dhis.mobile.connection2;

import java.util.Hashtable;

/**
 * 
 * @author Paul Mark Castillo
 */
public class ParamsHTTP
    extends ParamsConn
{

    /**
     *
     */
    private String requestMethod;

    /**
     *
     */
    private Hashtable requestProperty;

    /**
     *
     */
    public ParamsHTTP()
    {
        requestProperty = new Hashtable();
    }

    /**
     * @return the requestMethod
     */
    public String getRequestMethod()
    {
        return requestMethod;
    }

    /**
     * @param requestMethod the requestMethod to set
     */
    public void setRequestMethod( String requestMethod )
    {
        this.requestMethod = requestMethod;
    }

    /**
     * @return the requestProperty
     */
    public Hashtable getRequestProperty()
    {
        return requestProperty;
    }

    /**
     * @param requestProperty the requestProperty to set
     */
    public void setRequestProperty( Hashtable requestProperty )
    {
        this.requestProperty = requestProperty;
    }
}
