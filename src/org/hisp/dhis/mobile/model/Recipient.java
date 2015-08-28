package org.hisp.dhis.mobile.model;

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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

public class Recipient
    implements DataStreamSerializable
{
    private Vector users = new Vector();

    public Vector getUsers()
    {
        return users;
    }

    public void setUsers( Vector users )
    {
        this.users = users;
    }

    public void serialize( DataOutputStream dout )
        throws IOException
    {
        if ( users != null )
        {
            dout.writeInt( users.size() );

            for ( int i = 0; i < users.size(); i++ )
            {
                User user = (User) users.elementAt( i );
                user.serialize( dout );
            }
        }
        else
        {
            dout.writeInt( 0 );
        }

    }

    public void deSerialize( DataInputStream din )
        throws IOException
    {
        int numbId = din.readInt();
        if ( numbId > 0 )
        {
            Vector usersVector = new Vector();
            for ( int i = 0; i < numbId; i++ )
            {
                User user = new User();
                user.deSerialize( din );
                usersVector.addElement( user );
            }
            this.setUsers( usersVector );
        }
        else
        {
            this.setUsers( new Vector() );
        }

    }

}