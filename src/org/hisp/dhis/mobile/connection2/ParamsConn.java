package org.hisp.dhis.mobile.connection2;

import javax.microedition.io.Connector;

/**
 * 
 * @author Paul Mark Castillo
 */
public class ParamsConn
{

    /**
     *
     */
    private String schemeName;

    /**
     *
     */
    private String hierarchicalPart;

    /**
     *
     */
    private String query;

    /**
     * 
     */
    private String fragment;

    /**
     * 
     */
    private String URI;

    /*
     *
     */
    private int mode;

    /**
     *
     */
    private boolean timeouts;

    /**
     *
     */
    public ParamsConn()
    {
        setMode( Connector.READ_WRITE );
        setTimeouts( true );
    }

    /**
     * @return the scheme
     */
    public String getSchemeName()
    {
        return schemeName;
    }

    /**
     * @param scheme the scheme to set
     */
    public void setSchemeName( String scheme )
    {
        this.schemeName = scheme;
    }

    /**
     * @return the target
     */
    public String getHierarchicalPart()
    {
        return hierarchicalPart;
    }

    /**
     * @param target the target to set
     */
    public void setHierarchicalPart( String target )
    {
        this.hierarchicalPart = target;
    }

    /**
     * @return the params
     */
    public String getQuery()
    {
        return query;
    }

    /**
     * @param params the params to set
     */
    public void setQuery( String params )
    {
        this.query = params;
    }

    /**
     * @return the mode
     */
    public int getMode()
    {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode( int mode )
    {
        this.mode = mode;
    }

    /**
     * @return the timeouts
     */
    public boolean isTimeouts()
    {
        return timeouts;
    }

    /**
     * @param timeouts the timeouts to set
     */
    public void setTimeouts( boolean timeouts )
    {
        this.timeouts = timeouts;
    }

    /**
     * 
     * @return
     */
    public String getFragment()
    {
        return fragment;
    }

    /**
     * 
     * @param fragment
     */
    public void setFragment( String fragment )
    {
        this.fragment = fragment;
    }

    /**
     * 
     * @return
     */
    public String getURI()
    {
        if ( URI == null )
        {
            return getSchemeName() + ":" + getHierarchicalPart() + getQuery();
        }
        else
        {
            return URI;
        }
    }

    /**
     * 
     * @param uRI
     */
    public void setURI( String uRI )
    {
        URI = uRI;
    }
}
