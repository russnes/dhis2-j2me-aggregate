package org.hisp.dhis.mobile.connection2;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.hisp.dhis.mobile.log.LogMan;

/**
 * 
 * @author Paul Mark Castillo
 */
public class ManagerIO
{

    /**
     * 
     */
    public static final String CLASS_TAG = "ManagerIO";

    /**
     *
     */
    public static final String STRING_ENC = "UTF-8";

    /**
     * 
     * @param os
     * @param b
     * @throws IOException
     */
    public void write( OutputStream os, byte b[] )
        throws IOException
    {
        os.write( b, 0, b.length );
    }

    /**
     * 
     * @param os
     * @param c
     * @throws IOException
     */
    public void write( OutputStream os, char c[] )
        throws IOException
    {
        OutputStreamWriter osw = new OutputStreamWriter( os, STRING_ENC );
        osw.write( c, 0, c.length );
    }

    /**
     * 
     * @param is
     * @return
     * @throws IOException
     */
    public byte[] readBinary( InputStream is, ProgressIO progress )
        throws IOException
    {
        int available = is.available();

        if ( available > 0 )
        {
        }
        else
        {
            LogMan.log( LogMan.WARN, "IO," + CLASS_TAG, "InputStream not available!" );
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        long length = 0;
        int byteData;
        while ( (byteData = is.read()) != -1 )
        {
            if ( progress != null && progress.isRealtimeUpdate() )
            {
                progress.setReadBytes( length );
            }
            baos.write( byteData );
            length++;
        }

        byte b[] = baos.toByteArray();

        baos.close();
        baos = null;

        return b;
    }

    /**
     * 
     * @param is
     * @param length
     * @return
     * @throws IOException
     */
    public byte[] readBinaryFully( InputStream is, long length, ProgressIO progress )
        throws IOException
    {
        if ( progress != null )
        {
            progress.setTotalBytes( length );
        }

        int available = is.available();

        if ( available > 0 )
        {
        }
        else
        {
            LogMan.log( LogMan.WARN, "IO", "InputStream not available!" );
        }

        int actual = 0;
        int bytesread = 0;
        byte[] data = new byte[(int) length];
        while ( (bytesread != length) && (actual != -1) )
        {
            if ( progress != null )
            {
                progress.setReadBytes( bytesread );
            }
            actual = is.read( data, bytesread, (int) length - bytesread );
            bytesread += actual;
        }

        return data;

    }

    /**
     * 
     * @param is
     * @return
     * @throws IOException
     */
    public String readTexts( InputStream is, ProgressIO progress )
        throws IOException
    {
        int available = is.available();

        if ( available > 0 )
        {
        }
        else
        {
            LogMan.log( LogMan.WARN, "IO", "InputStream not available!" );
        }

        InputStreamReader isr = new InputStreamReader( is, STRING_ENC );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        long length = 0;
        int byteData;
        while ( (byteData = isr.read()) != -1 )
        {
            if ( progress != null )
            {
                progress.setReadBytes( length );
            }
            baos.write( byteData );
            length++;
        }

        byte b[] = baos.toByteArray();
        String str = new String( b );

        return str;

    }

    /**
     * 
     * @param dis
     * @param length
     * @return
     * @throws IOException
     */
    public String readTextsFully( DataInputStream dis, long length, ProgressIO progress )
        throws IOException
    {
        if ( progress != null )
        {
            progress.setTotalBytes( length );
        }

        int available = dis.available();

        if ( available > 0 )
        {
        }
        else
        {
            LogMan.log( LogMan.WARN, "IO", "InputStream not available!" );
        }

        byte b[] = new byte[(int) length];
        dis.readFully( b, 0, b.length );

        if ( progress != null )
        {
            progress.setReadBytes( length );
        }

        String str = new String( b, STRING_ENC );

        return str;
    }

    /**
     *
     */
    public void close( InputStream is )
        throws IOException
    {
        is.close();
        is = null;
    }

    /**
     *
     */
    public void close( OutputStream os )
        throws IOException
    {
        os.close();
        os = null;
    }
}
