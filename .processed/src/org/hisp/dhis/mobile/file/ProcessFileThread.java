package org.hisp.dhis.mobile.file;

import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.IllegalModeException;

/**
 * 
 * @author Paul Mark Castillo
 *
 */
public class ProcessFileThread
    extends Thread
{

    /**
     *
     */
    FileManager fileManager;

    /**
     *
     */
    AlertDisplayer alertDisplayer;

    /**
     *
     */
    String path;

    /**
     *
     */
    CallbackSaveFile callback;

    /**
     *
     */
    FileConnection fileConnection;

    /**
     *
     */
    OutputStream os;

    /**
     * 
     * @param fileManager
     * @param callback
     */
    public ProcessFileThread( FileManager fileManager, String path, CallbackSaveFile callback )
    {
        this.fileManager = fileManager;
        this.path = path;
        this.callback = callback;

        alertDisplayer = fileManager.getAlertDisplayer();
    }

    /**
     * 
     */
    public void run()
    {
        boolean okOpen = openConnection( path );
        if ( okOpen )
        {
            boolean okOutput = openOutputStream( fileConnection );
            if ( okOutput )
            {
                callback.saveFile( os );

                try
                {
                    fileConnection.close();
                }
                catch ( Exception e )
                {
                }
                finally
                {
                    fileConnection = null;
                }

                fileManager.exit();
            }
        }
    }

    /**
     * 
     * @param path
     * @return
     */
    public boolean openConnection( String path )
    {
        try
        {
            fileConnection = (FileConnection) Connector.open( path );

            if ( fileConnection.exists() )
            {
                fileConnection.delete();
                fileConnection = (FileConnection) Connector.open( path );
                fileConnection.create();
            }
            else
            {
                fileConnection.create();
            }

            return true;
        }
        catch ( IllegalArgumentException iae )
        {
            alertDisplayer.dispException( iae, "a parameter is invalid." );
        }
        catch ( ConnectionNotFoundException cnfe )
        {
            alertDisplayer.dispException( cnfe,
                "the requested connection cannot be made, or the protocol type does not exist." );
        }
        catch ( IOException ioe )
        {
            alertDisplayer.dispException( ioe, "some other kind of I/O error occurs." );
        }
        catch ( SecurityException se )
        {
            alertDisplayer.dispException( se, "a requested protocol handler is not permitted." );
        }
        catch ( Exception e )
        {
            alertDisplayer.dispException( e, "" );
        }
        return false;
    }

    /**
     * 
     * @param fileConnection
     * @return
     */
    private boolean openOutputStream( FileConnection fileConnection )
    {
        try
        {
            os = fileConnection.openOutputStream();
            return true;
        }
        catch ( IOException ioe )
        {
            alertDisplayer
                .dispException(
                    ioe,
                    "an I/O error occurs, if the method is invoked on a directory, the file does not yet exist, or the connection's target is not accessible." );
        }
        catch ( IllegalModeException ime )
        {
            alertDisplayer
                .dispException(
                    ime,
                    "the application does have write access to the connection's target but has opened the connection in Connector.READ mode." );
        }
        catch ( SecurityException se )
        {
            alertDisplayer
                .dispException( se, "the application is not granted write access to the connection's target." );
        }
        catch ( Exception e )
        {
            alertDisplayer.dispException( e, "" );
        }
        return false;
    }
}
