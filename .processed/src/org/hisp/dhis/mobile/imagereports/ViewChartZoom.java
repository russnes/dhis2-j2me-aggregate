package org.hisp.dhis.mobile.imagereports;

import java.io.OutputStream;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

import org.hisp.dhis.mobile.file.CallbackSaveFile;
import org.hisp.dhis.mobile.file.FileManager;
import org.hisp.dhis.mobile.log.LogMan;

/**
 * 
 * @author Paul Mark Castillo
 * 
 */
public class ViewChartZoom
    extends Canvas
    implements CommandListener, CallbackSaveFile
{
    /**
     * 
     */
    private static final String CLASS_TAG = "ViewChartZoom";

    /**
     * 
     */
    MIDlet midlet;

    /**
     * 
     */
    Displayable previousDisplayable;

    /**
     * 
     */
    ChartData imageFile;

    /**
     * 
     */
    int offsetX = 0, offsetY = 0;

    /**
     * 
     */
    int step = 50;

    /**
     * 
     */
    int canvasWidth, canvasHeight;

    /**
     * 
     */
    int imageWidth, imageHeight;

    /**
     * 
     */
    Command cmdBack = new Command( "Back", Command.BACK, 0 );

    /**
     * 
     */
    Command cmdSave = new Command( "Save", Command.OK, 0 );

    /**
     * 
     */
    String help = "Use ARROW KEYS to Move Around";

    /**
     * 
     */
    boolean firstTime = true;

    /**
     * 
     */
    public ViewChartZoom( MIDlet midlet, Displayable previousDisplayable, ChartData imageFile )
    {
        this.midlet = midlet;
        this.previousDisplayable = previousDisplayable;
        this.imageFile = imageFile;

        LogMan.log( LogMan.DEBUG, "UI,ImageReports," + CLASS_TAG, "Initializing ViewChartZoom Screen..." );

        setFullScreenMode( false );

        canvasWidth = getWidth();
        canvasHeight = getHeight();
        imageWidth = imageFile.getImage().getWidth();
        imageHeight = imageFile.getImage().getHeight();

        addCommand( cmdBack );
        addCommand( cmdSave );
        setCommandListener( this );

        int canvasWidthHalf = canvasWidth / 2;
        int imageWidthHalf = imageWidth / 2;

        int centerOffset = 0 - imageWidthHalf + canvasWidthHalf;

        offsetX = centerOffset;
    }

    /**
     * 
     */
    protected void paint( Graphics g )
    {
        g.setColor( 255, 255, 255 );

        g.setClip( 0, 0, getWidth(), getHeight() );
        g.fillRect( 0, 0, getWidth(), getHeight() );

        g.drawImage( imageFile.getImage(), offsetX, offsetY, Graphics.TOP | Graphics.LEFT );

        if ( firstTime )
        {
            LogMan.log( LogMan.DEBUG, "UI,ImageReports," + CLASS_TAG, "Drawing HELP for the first time..." );
            drawHelp( g );
        }
    }

    /**
     * 
     * @param g
     */
    private void drawHelp( Graphics g )
    {
        int padding = 10;

        Font f = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL );
        g.setFont( f );

        int textWidth = f.stringWidth( help );
        int textHeight = f.getHeight();

        int boxHeight = padding + textHeight + padding;
        int boxWidth = padding + textWidth + padding;

        int boxY = (int) ((double) ((canvasHeight - boxHeight) / 1.125));
        int boxX = (int) ((double) ((canvasWidth - boxWidth) / 2));

        g.setColor( 0, 0, 0 );
        g.fillRect( boxX, boxY, boxWidth, boxHeight );
        g.setColor( 255, 255, 255 );
        g.drawString( help, boxX + padding, boxY + padding, Graphics.TOP | Graphics.LEFT );
    }

    /**
     * 
     */
    protected void keyPressed( int keyCode )
    {

        switch ( getGameAction( keyCode ) )
        {
        case UP:
            LogMan.log( LogMan.DEBUG, "UI,ImageReports,Keypressed" + CLASS_TAG, "UP Pressed" );
            
            int y1 = offsetY + step;

            if ( y1 < 0 )
            {
                offsetY += step;
            }
            else
            {
                offsetY = 0;
            }
            break;
        case DOWN:
            LogMan.log( LogMan.DEBUG, "UI,ImageReports,Keypressed" + CLASS_TAG, "DOWN Pressed" );

            int y2 = offsetY + imageHeight - step;

            if ( y2 > canvasHeight )
            {
                offsetY -= step;
            }
            else
            {
                offsetY = (0 - imageHeight) + canvasHeight;
            }
            break;
        case LEFT:
            LogMan.log( LogMan.DEBUG, "UI,ImageReports,Keypressed" + CLASS_TAG, "LEFT Pressed" );
            
            int x1 = offsetX + step;

            if ( x1 < 0 )
            {
                offsetX += step;
            }
            else
            {
                offsetX = 0;
            }
            break;
        case RIGHT:
            LogMan.log( LogMan.DEBUG, "UI,ImageReports,Keypressed" + CLASS_TAG, "RIGHT Pressed" );
            
            int x2 = offsetX + imageWidth - step;

            if ( x2 > canvasWidth )
            {
                offsetX -= step;
            }
            else
            {
                offsetX = (0 - imageWidth) + canvasWidth;
            }
            break;
        }

        if ( firstTime )
        {
            firstTime = false;
        }
        
        repaint();
    }

    /**
     * 
     */
    protected void keyRepeated( int keyCode )
    {
        keyPressed( keyCode );
    }

    /**
     * 
     */
    public void commandAction( Command c, Displayable d )
    {
        if ( c == cmdBack )
        {
            LogMan.log( LogMan.DEBUG, "UI,ImageReports," + CLASS_TAG, "Back Command Pressed" );
            
            Display.getDisplay( midlet ).setCurrent( previousDisplayable );
        }
        else if ( c == cmdSave )
        {
            LogMan.log( LogMan.DEBUG, "UI,ImageReports," + CLASS_TAG, "Save Command Pressed" );
            
            FileManager fm = new FileManager( midlet );
            fm.saveFiles( imageFile.getFileName() + " - HighRes" + "." + imageFile.getFileExtension(), this );
        }
    }

    /**
     * 
     */
    public void saveFile( OutputStream os )
    {
        LogMan.log( LogMan.DEBUG, "UI,ImageReports," + CLASS_TAG, "Saving to File..." );

        ProgressAlertFile alert = new ProgressAlertFile( midlet, os, -1 );
        alert.show();
        alert.write( imageFile.getData() );
        alert.closeStream();
    }
}
