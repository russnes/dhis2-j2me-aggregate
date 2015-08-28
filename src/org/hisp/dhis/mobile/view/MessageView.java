package org.hisp.dhis.mobile.view;

/*
 * Copyright (c) 2004-2014, University of Oslo All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. * Neither the name of the HISP project nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.FacilityMIDlet;
import org.hisp.dhis.mobile.model.Message;
import org.hisp.dhis.mobile.model.Recipient;
import org.hisp.dhis.mobile.recordstore.StringRecordStore;
import org.hisp.dhis.mobile.recordstore.RecipientRecordStore;
import org.hisp.dhis.mobile.ui.Text;

public class MessageView
    extends AbstractView
    implements CommandListener
{
    private TextBox messageTextBox;

    private FacilityMIDlet facilityMIDlet;

    private Command sendCommand;

    private Command backCommand;

    private Vector recipientVector;

    private String subject;

    private Message message;

    private Recipient recipient;

    public MessageView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.facilityMIDlet = (FacilityMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        this.messageTextBox = null;
        this.sendCommand = null;
        this.backCommand = null;
        this.recipientVector = null;
        this.subject = null;
        this.message = null;
        this.recipient = null;
        System.gc();
    }

    public void showView()
    {
        prepareView();
        this.switchDisplayable( null, this.getMessageTextBox() );
    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == this.getBackCommand() )
        {
            dhisMIDlet.getMessageSubjectView().showView();
        }
        else if ( command == this.getSendCommand() )
        {
            try
            {
                ConnectionManager.setUrl( dhisMIDlet.getCurrentOrgUnit().getSendMessageUrl() );
                ConnectionManager.sendMessage( getMessage() );

            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }

        }

    }

    public TextBox getMessageTextBox()
    {
        if ( messageTextBox == null )
        {
            messageTextBox = new TextBox( "Enter message", "", 9999, 0 );
            messageTextBox.addCommand( this.getBackCommand() );
            messageTextBox.addCommand( this.getSendCommand() );

            messageTextBox.setCommandListener( this );
        }
        return messageTextBox;
    }

    public void setMessageTextBox( TextBox messageTextBox )
    {
        this.messageTextBox = messageTextBox;
    }

    public Command getSendCommand()
    {
        if ( sendCommand == null )
        {
            sendCommand = new Command( Text.SEND(), Command.SCREEN, 0 );
        }
        return sendCommand;
    }

    public void setSendCommand( Command sendCommand )
    {
        this.sendCommand = sendCommand;
    }

    public Command getBackCommand()
    {
        if ( backCommand == null )
        {
            backCommand = new Command( Text.BACK(), Command.BACK, 0 );
        }
        return backCommand;
    }

    public void setBackCommand( Command backCommand )
    {
        this.backCommand = backCommand;
    }

    public Vector getRecipientVector()
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        if ( recipientVector == null )
        {
            recipientVector = RecipientRecordStore.loadRecipients();
        }
        return recipientVector;
    }

    public void setRecipientVector( Vector recipientVector )
    {
        this.recipientVector = recipientVector;
    }

    public Message getMessage()
        throws RecordStoreNotOpenException, RecordStoreException, IOException
    {
        if ( message == null )
        {
            message = new Message();
            message.setSubject( this.getSubject() );
            message.setText( this.getMessageTextBox().getString() );
            message.setRecipient( this.getRecipient() );
        }

        return message;
    }

    public void setMessage( Message message )
    {
        this.message = message;
    }

    public String getSubject()
        throws RecordStoreNotOpenException, RecordStoreException
    {
        if ( subject == null )
        {
            subject = StringRecordStore.load();
        }
        return subject;
    }

    public void setSubject( String subject )
    {
        this.subject = subject;
    }

    public Recipient getRecipient()
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        if ( recipient == null )
        {
            recipient = new Recipient();
            recipient.setUsers( RecipientRecordStore.loadRecipients() );
        }
        return recipient;
    }

    public void setRecipient( Recipient recipient )
    {
        this.recipient = recipient;
    }

}
