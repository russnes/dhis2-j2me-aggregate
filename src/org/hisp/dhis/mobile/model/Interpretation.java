package org.hisp.dhis.mobile.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

public class Interpretation
    implements DataStreamSerializable
{
    private int id;

    private String text;

    private Vector interComments;

    public void serialize( DataOutputStream dout )
        throws IOException
    {
        dout.writeInt( id );
        dout.writeUTF( text );

        if ( interComments != null )
        {
            dout.writeInt( interComments.size() );

            for ( int i = 0; i < interComments.size(); i++ )
            {
                InterpretationComment interComment = (InterpretationComment) interComments.elementAt( i );
                interComment.serialize( dout );
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
        this.setId( din.readInt() );
        this.setText( din.readUTF() );

        int numbId = din.readInt();
        if ( numbId > 0 )
        {
            Vector interCommentVector = new Vector();
            for ( int i = 0; i < numbId; i++ )
            {
                InterpretationComment interComment = new InterpretationComment();
                interComment.deSerialize( din );
                interCommentVector.addElement( interComment );
            }
            this.setInterComments( interCommentVector );
        }
        else
        {
            this.setInterComments( new Vector() );
        }
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
    }

    public Vector getInterComments()
    {
        return interComments;
    }

    public void setInterComments( Vector interComments )
    {
        this.interComments = interComments;
    }

}
