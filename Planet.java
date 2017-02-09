package org.artoolkit.ar.samples.ARSimple;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;


public class Planet
{
    FloatBuffer vertexData;
    FloatBuffer normalData;
    FloatBuffer colorData;
    FloatBuffer textureData;

    float blue	= 1.0f;
    float red	= 1.0f;
    float green= 1.0f;
    float alpha = 1.0f;

    float scale;
    float squash;
    float radius;
    int stacks, slices;
    public float[] postition = {0.0f, 0.0f, 0.0f};


    public Planet(int stacks, int slices, float radius, float squash, GL10 gl, Context context, boolean imageId, int resourceId)
    {
        this.stacks = stacks;
        this.slices = slices;
        this.radius = radius;
        this.squash = squash;
        init(this.stacks, this.slices,radius,squash, gl, context, imageId, resourceId);
    }

    public void setColor4f(float r, float g, float b, float a)
    {
        red = r;
        green = g;
        blue = b;
        alpha = a;
    }

    private void init(int stacks,int slices, float radius, float squash, GL10 gl, Context context, boolean imageId, int resourceId)
    {
        float[] vertexData;
        float[] normalData;
        float[] colorData;
        float[] textData=null;

        float colorIncrement=0f;

        int vIndex=0;				//vertex index
        int cIndex=0;				//color index
        int nIndex=0;				//normal index
        int tIndex=0;				//texture index

        if(imageId == true)
        {
            createTexture(gl, context, resourceId);
        }

        scale=radius;
        this.squash=squash;


        //Vertices
        vertexData = new float[ 3*((slices*2+2) * stacks)];

        //Color data
        colorData = new float[ (4*(slices*2+2) * stacks)];

        //Normal pointers for lighting
        normalData = new float[3*((slices*2+2) * stacks)];

        if(imageId == true)
            textData = new float [2 * (( this.slices*2+2) * (this.stacks))];

        //Latitude
        for(int phiIdx=0; phiIdx < stacks; phiIdx++)
        {
            float phi0 = (float)Math.PI * ((float)(phiIdx+0) * (1.0f/(float)(stacks)) - 0.5f);
            float phi1 = (float)Math.PI * ((float)(phiIdx+1) * (1.0f/(float)(stacks)) - 0.5f);

            float cosPhi0 = (float)Math.cos(phi0);
            float sinPhi0 = (float)Math.sin(phi0);
            float cosPhi1 = (float)Math.cos(phi1);
            float sinPhi1 = (float)Math.sin(phi1);

            float cosTheta, sinTheta;

            //Longitude
            for(int thetaIdx=0; thetaIdx < slices; thetaIdx++)
            {
                float theta = (float) (2.0f*(float)Math.PI * ((float)thetaIdx) * (1.0/(float)(slices-1)));
                cosTheta = (float)Math.cos(theta);
                sinTheta = (float)Math.sin(theta);

                vertexData[vIndex]   = radius*cosPhi0*cosTheta;
                vertexData[vIndex+1] = radius*(sinPhi0*squash);
                vertexData[vIndex+2] = radius*(cosPhi0*sinTheta);

                vertexData[vIndex+3] = radius*cosPhi1*cosTheta;
                vertexData[vIndex+4] = radius*(sinPhi1*squash);
                vertexData[vIndex+5] = radius*(cosPhi1*sinTheta);

                normalData[nIndex] = (float)(cosPhi0 * cosTheta);
                normalData[nIndex+2] = cosPhi0 * sinTheta;
                normalData[nIndex+1] = sinPhi0;

                normalData[nIndex+3] = cosPhi1 * cosTheta;
                normalData[nIndex+5] = cosPhi1 * sinTheta;
                normalData[nIndex+4] = sinPhi1;

                if(textData != null)
                {
                    float texX = (float)thetaIdx * (1.0f/(float)( this.slices-1));
                    textData [tIndex + 0] = texX;
                    textData [tIndex + 1] = (float)(phiIdx+0) * (1.0f/(float)(this.stacks));
                    textData [tIndex + 2] = texX;
                    textData [tIndex + 3] = (float)(phiIdx+1) * (1.0f/(float)(this.stacks));
                }

                colorData[cIndex] = red;
                colorData[cIndex+1] = green;
                colorData[cIndex+2] = blue;
                colorData[cIndex+4] = red;
                colorData[cIndex+5] = green;
                colorData[cIndex+6] = blue;
                colorData[cIndex+3] = alpha;
                colorData[cIndex+7] = alpha;

                cIndex+=2*4;
                vIndex+=2*3;
                nIndex+=2*3;

                if(textData!=null)
                    tIndex+= 2*2;

                vertexData[vIndex+0] = vertexData[vIndex+3] = vertexData[vIndex-3];
                vertexData[vIndex+1] = vertexData[vIndex+4] = vertexData[vIndex-2];
                vertexData[vIndex+2] = vertexData[vIndex+5] = vertexData[vIndex-1];

                normalData[nIndex+0] = normalData[nIndex+3] = normalData[nIndex-3];
                normalData[nIndex+1] = normalData[nIndex+4] = normalData[nIndex-2];
                normalData[nIndex+2] = normalData[nIndex+5] = normalData[nIndex-1];

                if(textData!= null)
                {
                    textData [tIndex + 0] = textData [tIndex + 2] = textData [tIndex -2];
                    textData [tIndex + 1] = textData [tIndex + 3] = textData [tIndex -1];
                }

            }
        }

        this.vertexData = makeFloatBuffer(vertexData);
        this.normalData = makeFloatBuffer(normalData);
        this.colorData = makeFloatBuffer(colorData);

        if(textData!= null)
            textureData = makeFloatBuffer(textData);

    }

    protected static FloatBuffer makeFloatBuffer(float[] arr)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(arr);
        fb.position(0);
        return fb;
    }

    public void draw(GL10 gl)
    {

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        if(textureData != null)
        {
            gl.glEnable(GL10.GL_TEXTURE_2D);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureData);
        }

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.vertexData);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, this.normalData);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, this.colorData);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, (slices + 1) * 2 * (stacks - 1) + 2);

        gl.glDisable(GL10.GL_BLEND);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    private int[] textures = new int[1];

    public int createTexture(GL10 gl, Context contextRegf, int resource)
    {
        Bitmap tempImage = BitmapFactory.decodeResource(contextRegf.getResources(), resource);
        gl.glGenTextures(1, textures, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, tempImage, 0);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        tempImage.recycle();

        return resource;
    }
}