package org.hisp.dhis.mobile.imagereports;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.hisp.dhis.mobile.log.LogMan;

/**
 * 
 * @author Paul Mark Castillo
 * 
 */
public class UtilUI
{
    /**
     * 
     */
    private static final String CLASS_TAG = "UtilUI";

    /**
     * Based on: http://www.coderanch.com/t/228899/JME/Mobile/we-resize-image-me
     * 
     * @param image
     * @param resizedWidth
     * @param resizedHeight
     * @return
     */
    public static Image resizeImage( Image image, int resizedWidth, int resizedHeight )
    {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        int inputImageData[] = new int[originalWidth];
        int outputImageData[] = new int[resizedWidth * resizedHeight];

        int dataY, dataX;
        for ( int y = 0; y < resizedHeight; y++ )
        {
            dataY = y * originalHeight / resizedHeight;

            image.getRGB( inputImageData, 0, originalWidth, 0, dataY, originalWidth, 1 );

            for ( int x = 0; x < resizedWidth; x++ )
            {
                dataX = x * originalWidth / resizedWidth;
                outputImageData[(resizedWidth * y) + x] = inputImageData[dataX];
            }
        }
        return Image.createRGBImage( outputImageData, resizedWidth, resizedHeight, true );
    } // end of resizeImage

    /**
     * 
     * @param image
     * @param resizedWidth
     * @param resizedHeight
     * @return
     */
    public static Image scaleImage( Image image, int allowableWidth, int allowableHeight )
    {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        int proposedHeight = scaleHeight( imageWidth, imageHeight, allowableWidth );
        if ( proposedHeight > allowableHeight )
        {
            int proposedWidth = scaleWidth( imageWidth, imageHeight, allowableHeight );
            return resizeImage( image, proposedWidth, allowableHeight );
        }
        else
        {
            return resizeImage( image, allowableWidth, proposedHeight );
        }
    } // end of scaleImage

    /**
     * 
     * @param sourceWidth
     * @param sourceHeight
     * @param scaledHeight
     * @return
     */
    public static int scaleWidth( int sourceWidth, int sourceHeight, int scaledHeight )
    {
        return (int) (((double) sourceWidth / (double) sourceHeight) * (double) scaledHeight);
    } // end of scaleWidth

    /**
     * 
     * @param sourceWidth
     * @param sourceHeight
     * @param scaledWidth
     * @return
     */
    public static int scaleHeight( int sourceWidth, int sourceHeight, int scaledWidth )
    {
        return (int) (((double) sourceHeight / (double) sourceWidth) * (double) scaledWidth);
    }

    /**
     * Based on:
     * http://developer.nokia.com/community/wiki/Rotate_an_image_in_Java_ME
     * 
     * @param image
     * @param angle
     * @return
     * @throws Exception
     */
    public static Image rotateImage( Image image, int angle )
        throws Exception
    {
        if ( angle == 0 )
        {
            return image;
        }
        else if ( angle != 180 && angle != 90 && angle != 270 )
        {
            throw new Exception( "Invalid angle" );
        }

        int width = image.getWidth();
        int height = image.getHeight();

        int[] rowData = new int[width];
        int[] rotatedData = new int[width * height];

        int rotatedIndex = 0;

        for ( int i = 0; i < height; i++ )
        {
            image.getRGB( rowData, 0, width, 0, i, width, 1 );

            for ( int j = 0; j < width; j++ )
            {
                rotatedIndex = angle == 90 ? (height - i - 1) + j * height : (angle == 270 ? i + height
                    * (width - j - 1) : width * height - (i * width + j) - 1);

                rotatedData[rotatedIndex] = rowData[j];
            }
        }

        if ( angle == 90 || angle == 270 )
        {
            return Image.createRGBImage( rotatedData, height, width, true );
        }
        else
        {
            return Image.createRGBImage( rotatedData, width, height, true );
        }
    }

    /**
     *
     */
    public static int getScreenWidth()
    {
        int width = getDummyCanvas().getWidth();

        LogMan.log( LogMan.DEBUG, "UI," + CLASS_TAG, "Full Screen Width: " + width );

        return width;
    }

    /**
     *
     */
    public static int getScreenHeight()
    {
        int height = getDummyCanvas().getHeight();

        LogMan.log( LogMan.DEBUG, "UI," + CLASS_TAG, "Full Screen Height: " + height );

        return height;
    }

    /**
     * 
     * @return
     */
    private static Canvas getDummyCanvas()
    {
        Canvas dummyCanvas = new Canvas()
        {

            protected void paint( Graphics grphcs )
            {
            }
        };
        dummyCanvas.setFullScreenMode( false );
        return dummyCanvas;
    }

    /**
     * 
     * @param total
     * @param fraction
     * @return
     */
    public static long fraction( long total, int fraction )
    {
        for ( int i = 0; i <= fraction; i++ )
        {
            total = total / 2;
        }

        LogMan.log( LogMan.DEBUG, "UI," + CLASS_TAG, total + " factored in " + fraction + " is " + total );

        return total;
    }

    /**
     * 
     * @param img
     * @return
     */
    public static byte[] convertImageToByteArray( Image img )
    {
        int[] imgRgbData = new int[img.getWidth() * img.getHeight()];
        byte[] imageData = null;

        try
        {
            img.getRGB( imgRgbData, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight() );
        }
        catch ( Exception e )
        {
            LogMan.log( "UI,Image," + CLASS_TAG, e );
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream( baos );

        try
        {
            dos.writeInt( imgRgbData.length );
            dos.writeInt( img.getWidth() );
            dos.writeInt( img.getHeight() );

            for ( int i = 0; i < imgRgbData.length; i++ )
            {
                dos.writeInt( imgRgbData[i] );
            }

            imageData = baos.toByteArray();
            baos.close();
            dos.close();
        }
        catch ( Exception e )
        {
            LogMan.log( "UI,Image," + CLASS_TAG, e );
            e.printStackTrace();
        }
        return imageData;
    }
}
