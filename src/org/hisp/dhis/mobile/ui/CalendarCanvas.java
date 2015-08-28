package org.hisp.dhis.mobile.ui;

/*
 * Copyright (c) 2004-2013, University of Oslo All rights reserved.
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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import org.hisp.dhis.mobile.midlet.FacilityMIDlet;

public class CalendarCanvas
    extends Canvas
    implements CommandListener
{
    CalendarWidget calendar = null;

    private FacilityMIDlet dhisMIDlet;

    Command selectDateCmd = null;

    public CalendarCanvas( FacilityMIDlet dhisMIDlet )
    {
        this.dhisMIDlet = dhisMIDlet;
        calendar = new CalendarWidget( dhisMIDlet.getPeriodView().getDailyPeriodDateField().getDate() );
        calendar.initialize();
        selectDateCmd = new Command( "Select", Command.OK, 1 );
        setCommandListener( this );
        addCommand( selectDateCmd );
    }

    protected void keyPressed( int key )
    {
        int keyCode = getGameAction( key );
        if ( keyCode == FIRE )
        {
            dhisMIDlet.getPeriodView().showView();
        }
        else
        {
            calendar.keyPressed( keyCode );
            repaint();
        }
    }

    protected void paint( Graphics g )
    {
        g.setColor( 0xffffff );
        g.fillRect( 0, 0, getWidth(), getHeight() );
        calendar.paint( g );
    }

    public void commandAction( Command c, Displayable d )
    {

        if ( c == selectDateCmd )
        {
            if ( c.getCommandType() == 4 )
            {
                dhisMIDlet.getPeriodView().getDailyPeriodDateField().setDate( calendar.getSelectedDate() );
                dhisMIDlet.getPeriodView().showView();
            }
        }
    }
}
