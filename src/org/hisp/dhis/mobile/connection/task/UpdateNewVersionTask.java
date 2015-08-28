package org.hisp.dhis.mobile.connection.task;

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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;

public class UpdateNewVersionTask
    extends AbstractTask
{

    public static boolean isDownloadSuccessfully = false;

    public void run()
    {
        DHISMIDlet.setDownloading( true );
        InputStream inputStream = null;

        String filePath = "file:///";

        try
        {
            HttpConnection hcon = null;
            hcon = ConnectionManager.createConnection();
            int status = hcon.getResponseCode();
            if ( status == HttpConnection.HTTP_OK )
            {
                inputStream = hcon.openInputStream();
            }
            LoginTask.inputStream.close();

            if ( inputStream != null )
            {
                byte buf[] = new byte[1024];

                Enumeration en = FileSystemRegistry.listRoots();

                String root = (String) en.nextElement();
                filePath += root + "DHISMobile-Aggregate.jar";
                FileConnection fc = (FileConnection) Connector.open( filePath, Connector.READ_WRITE );
                if ( !fc.exists() )
                {
                    fc.create();
                }
                OutputStream dos = fc.openOutputStream();
                int len;
                while ( (len = inputStream.read( buf )) > 0 )
                    dos.write( buf, 0, len );
                dos.flush();
                fc.close();
                isDownloadSuccessfully = true;
            }

        }
        catch ( Exception e )
        {
            e.printStackTrace();
            isDownloadSuccessfully = false;
        }

    }

}
