package org.hisp.dhis.mobile.util;

import java.util.Vector;

/**
 * 
 * @author Paul Mark Castillo
 * 
 */
public class Utils
{
    /**
     * 
     * @param numbers
     * @return
     */
    public static double convertTwoDecimalsOnly( double numbers )
    {
        int adjustedNumber = (int) (numbers * 100);
        return ((double) adjustedNumber / (double) 100);
    }

    /**
     * 
     * @param initial
     * @param total
     * @return
     */
    public static int getPercentage( double initial, double total )
    {
        return (int) ((initial / total) * (double) 100);
    }

    /**
     * 
     * @param original
     * @param separator
     * @return
     */
    public static String[] split( String original, String separator )
    {
        Vector nodes = new Vector();

        // Parse nodes into vector
        int index = original.indexOf( separator );
        while ( index >= 0 )
        {
            nodes.addElement( original.substring( 0, index ) );
            original = original.substring( index + separator.length() );
            index = original.indexOf( separator );
        }

        // Get the last node
        nodes.addElement( original );

        // Create parse string array
        String[] result = new String[nodes.size()];
        if ( nodes.size() > 0 )
        {
            for ( int loop = 0; loop < nodes.size(); loop++ )
            {
                result[loop] = ((String) nodes.elementAt( loop )).trim();
            }
        }
        return result;
    }

    /**
     * 
     * @param aSource
     * @param aFrom
     * @param aTo
     * @return
     */
    public static String replace( String aSource, String aFrom, String aTo )
    {
        if ( aSource == null || aSource.length() == 0 || aFrom == null || aFrom.length() == 0 )
        {
            return null;
        }
        StringBuffer ret = new StringBuffer();
        String part1 = "";
        while ( aSource.indexOf( aFrom ) > -1 )
        {
            part1 = aSource.substring( 0, aSource.indexOf( aFrom ) ) + aTo;
            aSource = aSource.substring( aSource.indexOf( aFrom ) + aFrom.length() );
            ret.append( part1 );
        }
        ret.append( aSource );
        return ret.toString();
    }// end of replace
}
