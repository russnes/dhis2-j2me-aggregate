package org.hisp.dhis.mobile.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class InterpretationComment
    implements DataStreamSerializable
{
    private String text;

    public void serialize( DataOutputStream dout )
        throws IOException
    {

        dout.writeUTF( text );

    }

    public void deSerialize( DataInputStream din )
        throws IOException
    {
        this.setText( din.readUTF() );

    }

    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
    }

}
