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

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.hisp.dhis.mobile.view.AbstractView;

public class SplashScreen
    extends Canvas
{

    private Display display;

    private AbstractView nextView;

    private Image image;

    private Timer timer = new Timer();

    public SplashScreen( Image image, Display display, AbstractView nextView )
    {
        this.image = image;
        this.display = display;
        this.nextView = nextView;
        this.setFullScreenMode( true );
        this.display.setCurrent( this );
    }

    protected void keyPressed( int keyCode )
    {
        dismissSplashScreen();
    }

    protected void paint( Graphics g )
    {
        g.setColor( 66, 80, 115 );
        g.fillRect( 0, 0, getWidth(), getHeight() );
        if ( image != null )
            g.drawImage( image, getWidth() / 2, getHeight() / 2, Graphics.HCENTER | Graphics.VCENTER );
    }

    protected void pointerPressed( int x, int y )
    {
        dismissSplashScreen();
    }

    protected void showNotify()
    {
        timer.schedule( new CountDown(), 3000 );
    }

    private void dismissSplashScreen()
    {
        timer.cancel();
        nextView.showView();
    }

    // count down for the splash display
    private class CountDown
        extends TimerTask
    {
        public void run()
        {
            dismissSplashScreen();
        }
    }
}
