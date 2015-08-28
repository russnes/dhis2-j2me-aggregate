package org.hisp.dhis.mobile.imagereports;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.util.MemoryMonitor;
import org.hisp.dhis.mobile.util.Utils;
import org.json.me.JSONObject;

/**
 * 
 * @author Paul Mark Castillo
 * 
 */
public class ChartData
{
    /**
     * 
     */
    private static final String CLASS_TAG = "ChartData";

    /**
     * 
     */
    private String fileName;

    /**
     * 
     */
    private String fileExtension;

    /**
     * 
     */
    private byte[] data;

    /**
     * 
     */
    private JSONObject dataJSON;

    /**
     * 
     */
    private Image image;

    /**
     * 
     */
    private ImageItem imageItem;

    /**
     * 
     * @param fileName
     * @param fileExtension
     */
    public ChartData( String fileName, String fileExtension )
    {
        setFileName( fileName );
        setFileExtension( fileExtension );
    }

    /**
     * 
     * @param content_disposition
     */
    public void parseContentDisposition( String content_disposition )
    {
        LogMan.log( LogMan.DEBUG, "" + CLASS_TAG, "Parsing Content Disposition: " + content_disposition );

        if ( content_disposition != null )
        {
            if ( content_disposition.indexOf( ";" ) > 0 )
            {
                String[] process1 = Utils.split( content_disposition, ";" );

                if ( process1.length > 1 && process1[1].startsWith( "filename=" ) )
                {
                    String[] process2 = Utils.split( process1[1], "=" );
                    String process3 = Utils.replace( process2[1], "\"", "" );

                    if ( process3.indexOf( "." ) > 0 )
                    {
                        String[] process4 = Utils.split( process3, "." );

                        String fileName = process4[0];
                        String fileExtension = process4[1];

                        setFileName( fileName );
                        setFileExtension( fileExtension );
                    }
                    else
                    {
                        LogMan.log( LogMan.ERROR, "" + CLASS_TAG,
                            "Content Disposition Filename doesn't have an extension" );
                    }
                }
                else
                {
                    LogMan.log( LogMan.ERROR, "" + CLASS_TAG, "Content Disposition doesn't have 'filename=' symbol" );
                }
            }
            else
            {
                LogMan.log( LogMan.ERROR, "" + CLASS_TAG, "Content Disposition doesn't have ';' symbol" );
            }
        }
        else
        {
            LogMan.log( LogMan.ERROR, "" + CLASS_TAG, "Content Disposition is null!" );
        }
    }

    /**
     * 
     */
    public void generateImage()
    {
        LogMan.log( LogMan.INFO, "Image," + CLASS_TAG, "Creating Image: Memory Before: " );
        MemoryMonitor.outputMemory();

        Image img = Image.createImage( data, 0, data.length );
        setImage( img );

        ImageItem imgItem = new ImageItem( "", img, ImageItem.LAYOUT_DEFAULT, "" );
        setImageItem( imgItem );

        LogMan.log( LogMan.INFO, "Image," + CLASS_TAG, "Creating Image: Memory After: " );
        MemoryMonitor.outputMemory();
    }

    /**
     * 
     * @return
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * 
     * @param fileName
     */
    public void setFileName( String fileName )
    {
        this.fileName = fileName;
    }

    /**
     * 
     * @return
     */
    public String getFileExtension()
    {
        return fileExtension;
    }

    /**
     * 
     * @param fileExtension
     */
    public void setFileExtension( String fileExtension )
    {
        this.fileExtension = fileExtension;
    }

    /**
     * 
     * @return
     */
    public byte[] getData()
    {
        return data;
    }

    /**
     * 
     * @param data
     */
    public void setData( byte[] data )
    {
        this.data = data;
    }

    /**
     * 
     * @return
     */
    public Image getImage()
    {
        return image;
    }

    /**
     * 
     * @param image
     */
    public void setImage( Image image )
    {
        this.image = image;
    }

    /**
     * 
     * @return
     */
    public ImageItem getImageItem()
    {
        return imageItem;
    }

    /**
     * 
     * @param imageItem
     */
    public void setImageItem( ImageItem imageItem )
    {
        this.imageItem = imageItem;
    }

    /**
     * 
     * @return
     */
    public JSONObject getDataJSON()
    {
        return dataJSON;
    }

    /**
     * 
     * @param dataJSON
     */
    public void setDataJSON( JSONObject dataJSON )
    {
        this.dataJSON = dataJSON;
    }
}
