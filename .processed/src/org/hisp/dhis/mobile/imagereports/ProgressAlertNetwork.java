package org.hisp.dhis.mobile.imagereports;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import org.hisp.dhis.mobile.connection2.ConnectionStates;
import org.hisp.dhis.mobile.connection2.NetworkEvent;
import org.hisp.dhis.mobile.connection2.ProgressConn;
import org.hisp.dhis.mobile.connection2.ProgressListener;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.util.MemoryMonitor;
import org.hisp.dhis.mobile.util.Utils;

/**
 * 
 * @author Paul Mark Castillo
 * 
 */
public class ProgressAlertNetwork
    extends Alert
    implements ProgressListener, CommandListener
{
    /**
     * 
     */
    private static final String CLASS_TAG = "ProgressAlertNetwork";

    /**
     * 
     */
    private ProgressConn progress = new ProgressConn();

    /**
     * 
     */
    private boolean cancelable = false;

    /**
     * 
     */
    private boolean canceled = false, error = false;

    /**
     * 
     */
    private Command dummyCommand = new Command( "", Command.OK, 0 );

    /**
     * 
     */
    private Command okCommand = new Command( "Ok", Command.OK, 0 );

    /**
     * 
     */
    private Command cancelCommand = new Command( "Cancel", Command.CANCEL, 0 );

    /**
     * 
     */
    private MIDlet midlet;

    /**
     * 
     */
    private Displayable previousDisplayable;

    /**
     * 
     */
    private NetworkEvent networkEvent;

    /**
     * 
     */
    private String idleString = "", connectingString = "Connecting...", connectedString = "Connected",
        sendingString = "Sending...", sentString = "Sent", receivingString = "Receiving:", receivedString = "Received",
        disconnectingString = "Disconnecting...", disconnectedString = "Disconnected", errorString = "Error!",
        canceledString = "Canceled";

    /**
     * 
     * @param title
     */
    public ProgressAlertNetwork( boolean cancelable )
    {
        super( "Please wait..." );
        setType( AlertType.INFO );
        setTimeout( FOREVER );
        progress.setListener( this );
        this.cancelable = cancelable;

        if ( !this.cancelable )
        {
            addCommand( dummyCommand );
        }
        else
        {
            addCommand( cancelCommand );
        }

        setCommandListener( this );
    }

    /**
     * 
     */
    public void progressUpdated()
    {
        if ( !error && !canceled )
        {
            switch ( progress.getState() )
            {
            case ConnectionStates.IDLE:
                setString( getIdleString() );
                break;
            case ConnectionStates.CONNECTING:
                setString( getConnectingString() );
                break;
            case ConnectionStates.CONNECTED:
                setString( getConnectedString() );
                break;
            case ConnectionStates.SENDING:
                setString( getSendingString() );
                break;
            case ConnectionStates.SENT:
                setString( getSentString() );
                break;
            case ConnectionStates.RECEIVING:
                long received = progress.getReadBytes();
                long total = progress.getTotalBytes();

                String str;
                if ( total > 0 )
                {
                    str = MemoryMonitor.convertToReadableBytes( received ) + " / "
                        + MemoryMonitor.convertToReadableBytes( total ) + " " + Utils.getPercentage( received, total )
                        + "%";
                }
                else
                {
                    str = MemoryMonitor.convertToReadableBytes( received );
                }

                if ( progress.isRealtimeUpdate() )
                {
                    setString( getReceivingString() + ": " + str );
                }
                else
                {
                    setString( getReceivingString() );
                }

                break;
            case ConnectionStates.RECEIVED:
                setString( getReceivedString() );
                break;
            case ConnectionStates.DISCONNECTING:
                setString( getDisconnectingString() );
                break;
            case ConnectionStates.DISCONNECTED:
                setString( getDisconnectedString() );
                break;
            case ConnectionStates.ERROR:
                error = true;
                setType( AlertType.ERROR );
                setString( getErrorString() );
                break;
            case ConnectionStates.CANCELED:
                canceled = true;
                setType( AlertType.CONFIRMATION );
                setString( getCanceledString() );
                break;
            }
        }
    }

    /**
     * 
     */
    public void setString( String str )
    {
        super.setString( str );
    }

    /**
     * 
     */
    public void dismissAlert()
    {
        Display.getDisplay( midlet ).setCurrent( previousDisplayable );
    }

    /**
     * 
     */
    public void showOkCommand()
    {
        removeAllCommands();
        addCommand( okCommand );
    }

    /**
     * 
     */
    private void removeAllCommands()
    {
        removeCommand( dummyCommand );
        removeCommand( okCommand );
        removeCommand( cancelCommand );
    }

    /**
     * 
     */
    public void commandAction( Command c, Displayable d )
    {
        if ( c == okCommand )
        {
            LogMan.log( LogMan.DEBUG, "Network," + CLASS_TAG, "Ok command pressed" );
            dismissAlert();
        }
        else if ( c == cancelCommand )
        {
            LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, "Cancel command pressed" );
            networkEvent.disconnect( true );
        }
    }

    /**
     * 
     * @return
     */
    public ProgressConn getProgress()
    {
        return progress;
    }

    /**
     * 
     * @param progress
     */
    public void setProgress( ProgressConn progress )
    {
        this.progress = progress;
        this.progress.setListener( this );
    }

    /**
     * 
     * @return
     */
    public String getIdleString()
    {
        return idleString;
    }

    /**
     * 
     * @param idleString
     */
    public void setIdleString( String idleString )
    {
        this.idleString = idleString;
    }

    /**
     * 
     * @return
     */
    public String getConnectingString()
    {
        return connectingString;
    }

    /**
     * 
     * @param connectingString
     */
    public void setConnectingString( String connectingString )
    {
        this.connectingString = connectingString;
    }

    /**
     * 
     * @return
     */
    public String getConnectedString()
    {
        return connectedString;
    }

    /**
     * 
     * @param connectedString
     */
    public void setConnectedString( String connectedString )
    {
        this.connectedString = connectedString;
    }

    /**
     * 
     * @return
     */
    public String getSendingString()
    {
        return sendingString;
    }

    /**
     * 
     * @param sendingString
     */
    public void setSendingString( String sendingString )
    {
        this.sendingString = sendingString;
    }

    /**
     * 
     * @return
     */
    public String getSentString()
    {
        return sentString;
    }

    /**
     * 
     * @param sentString
     */
    public void setSentString( String sentString )
    {
        this.sentString = sentString;
    }

    /**
     * 
     * @return
     */
    public String getReceivingString()
    {
        return receivingString;
    }

    /**
     * 
     * @param receivingString
     */
    public void setReceivingString( String receivingString )
    {
        this.receivingString = receivingString;
    }

    /**
     * 
     * @return
     */
    public String getReceivedString()
    {
        return receivedString;
    }

    /**
     * 
     * @param receivedString
     */
    public void setReceivedString( String receivedString )
    {
        this.receivedString = receivedString;
    }

    /**
     * 
     * @return
     */
    public String getDisconnectingString()
    {
        return disconnectingString;
    }

    /**
     * 
     * @param disconnectingString
     */
    public void setDisconnectingString( String disconnectingString )
    {
        this.disconnectingString = disconnectingString;
    }

    /**
     * 
     * @return
     */
    public String getDisconnectedString()
    {
        return disconnectedString;
    }

    /**
     * 
     * @param disconnectedString
     */
    public void setDisconnectedString( String disconnectedString )
    {
        this.disconnectedString = disconnectedString;
    }

    /**
     * 
     * @return
     */
    public String getErrorString()
    {
        return errorString;
    }

    /**
     * 
     * @param errorString
     */
    public void setErrorString( String errorString )
    {
        this.errorString = errorString;
    }

    /**
     * 
     * @return
     */
    public String getCanceledString()
    {
        return canceledString;
    }

    /**
     * 
     * @param canceledString
     */
    public void setCanceledString( String canceledString )
    {
        this.canceledString = canceledString;
    }

    /**
     * 
     * @return
     */
    public MIDlet getMidlet()
    {
        return midlet;
    }

    /**
     * 
     * @param midlet
     */
    public void setMidlet( MIDlet midlet )
    {
        this.midlet = midlet;
    }

    /**
     * 
     * @return
     */
    public Displayable getPreviousDisplayable()
    {
        return previousDisplayable;
    }

    /**
     * 
     * @param previousDisplayable
     */
    public void setPreviousDisplayable( Displayable previousDisplayable )
    {
        this.previousDisplayable = previousDisplayable;
    }

    /**
     * 
     * @return
     */
    public NetworkEvent getNetworkEvent()
    {
        return networkEvent;
    }

    /**
     * 
     * @param networkEvent
     */
    public void setNetworkEvent( NetworkEvent networkEvent )
    {
        this.networkEvent = networkEvent;
    }
}
