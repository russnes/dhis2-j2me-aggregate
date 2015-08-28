package org.hisp.dhis.mobile.file;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;

/**
 * 
 * @author Paul Mark Castillo
 *
 */
public class ProcessRootsThread
    extends Thread
{

    /**
     * 
     */
    FileManager fileManager;

    /**
     * 
     */
    public ProcessRootsThread( FileManager fileManager )
    {
        this.fileManager = fileManager;
    }

    /**
     * 
     */
    public void run()
    {
        try
        {
            Enumeration enu = FileSystemRegistry.listRoots();
            Vector roots = new Vector();

            while ( enu.hasMoreElements() )
            {
                roots.addElement( (String) enu.nextElement() );
            }

            if ( roots.isEmpty() )
            {
                Alert noRootsAlert = new Alert( "Error", "No mounted root file systems available.", null,
                    AlertType.ERROR );
                Display.getDisplay( fileManager.getMidlet() ).setCurrent( noRootsAlert );
            }
            else
            {
                fileManager.showRoots( roots );
            }

        }
        catch ( SecurityException e )
        {
            e.printStackTrace();
            Alert noAPIAlert = new Alert( "Error", "SecurityException: " + e.getMessage() + " "
                + "Application is not given permission to read files.", null, AlertType.ERROR );
            Display.getDisplay( fileManager.getMidlet() ).setCurrent( noAPIAlert );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            Alert noAPIAlert = new Alert( "Error", "Exception: " + e.getMessage(), null, AlertType.ERROR );
            Display.getDisplay( fileManager.getMidlet() ).setCurrent( noAPIAlert );
        }
    }

}
