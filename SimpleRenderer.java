package org.artoolkit.ar.samples.ARSimple;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;
import org.artoolkit.ar.base.rendering.Cube;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * A very simple Renderer that adds a marker and draws a cube on it.
 */
public class SimpleRenderer extends ARRenderer {

    private int markerID = -1;
    private int markerID1 = -1;
    private int markerID2 = -1;
    private int markerID3 = -1;
    private Planet sun, earth, moon, jupiter;
    private float[] eyeposition = {0.0f, 0.0f, 10.0f};
    int resEarth, resSun, resMoon, resJupiter;
    public final static int SUN_LIGHT = GL10.GL_LIGHT0;
    float Mat_sun[];
    float Mat_sun_r[];
    float Mat_earth[];
    float Mat_moon[];
    float Mat_earth_r[];
    float Mat_jupiter[];
    float Dist_sun_earth[];
    float Dist_earth_moon[];
    float Dist_sun_jupiter[];
    float angle_sun_rotation = 0;
    float rotation_sun = 0.3f;
    float angle_earth = 0;
    float angle_earth_rotation = 0;
    float revolution_earth = 2.0f;
    float rotation_earth = 1;
    float angle_moon = 0;
    float angle_moon_rotation = 0;
    float revolution_moon = 0.5f;
    float rotation_moon = 0.3f;
    float angle_jupiter = 0;
    float angle_jupiter_rotation = 0;
    float revolution_jupiter = 0.8f;
    float rotation_jupiter = 0.8f;

    float[] light_ambient={0.2f, 0.2f, 0.2f, 1.0f};
    float[] light_diffuse={1.0f, 1.0f, 1.0f, 1.0f};
    float[] light_specular={1.0f, 1.0f, 1.0f, 1.0f};
    float[] sun_emission = {0.0f,0.0f,0.0f,1.0f};
    public Context context;
    public SimpleRenderer(Context context)
    {
        this.context = context;
    }
    private void initPlanet(GL10 gl)
    {
        resSun =  R.drawable.resource_sun;
        sun = new Planet(50, 50, 40.f, 1.0f, gl, this.context, true, resSun); // 크기 설정
        resEarth =  R.drawable.resource_earth;
        earth = new Planet(50, 50, 20f, 1.0f, gl, this.context, true, resEarth);
        resMoon = R.drawable.resource_moon;
        moon = new Planet(50, 50, 8f, 1.0f, gl, this.context, true, resMoon);
        resJupiter = R.drawable.resource_jupiter;
        jupiter = new Planet(50, 50, 30f, 1.0f, gl, this.context, true, resJupiter);
    }

    public void Init(GL10 gl)
    {
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE); // 원래 색상에 texture 곱해서 새로운 색상(조명효과 가능)
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_COLOR_MATERIAL);
    }

    private void Add_Light(GL10 gl, int Light_ID,float[] ambient, float[] diffuse, float[] specular)
    {
        float[] Pos={0.0f,0.0f,0.0f,1.0f};
        gl.glLightfv(Light_ID,GL10.GL_POSITION, makeFloatBuffer(Pos));
        gl.glLightfv(Light_ID,GL10.GL_AMBIENT, makeFloatBuffer(ambient));
        gl.glLightfv(Light_ID, GL10.GL_DIFFUSE, makeFloatBuffer(diffuse));
        gl.glLightfv(Light_ID, GL10.GL_SPECULAR, makeFloatBuffer(specular));
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(Light_ID);
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
    /**
     * Markers can be configured here.
     */
    @Override
    public boolean configureARScene() {

        markerID = ARToolKit.getInstance().addMarker("single;Data/marker1.patt;80");
        if (markerID < 0) return false;
        markerID1 = ARToolKit.getInstance().addMarker("single;Data/marker2.patt;80");
        if (markerID1 < 0) return false;
        markerID2 = ARToolKit.getInstance().addMarker("single;Data/marker3.patt;80");
        if (markerID2 < 0) return false;
        markerID3 = ARToolKit.getInstance().addMarker("single;Data/marker4.patt;80");
        if (markerID3 < 0) return false;
        return true;
    }

    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        gl.glViewport(0, 0, width, height);
        float aspectRatio;
        float zNear =.1f;
        float zFar =1000;
        float fieldOfView = 30.0f/57.3f;
        float size;

        aspectRatio=(float)width/(float)height;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        size = zNear * (float) (Math.tan((double)(fieldOfView/2.0f)));
        gl.glFrustumf(-size, size, -size /aspectRatio,size /aspectRatio, zNear, zFar);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        Init(gl);
        initPlanet(gl);
    }
    /**
     * Override the draw function from ARRenderer.
     */
    @Override
    public void draw(GL10 gl) {

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Color buffer 초기화
        gl.glClearDepthf(1.0f); // Depth buffer 초기화
        gl.glLoadIdentity();

        // Apply the ARToolKit projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadMatrixf(ARToolKit.getInstance().getProjectionMatrix(), 0);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glFrontFace(GL10.GL_CW);


        if (ARToolKit.getInstance().queryMarkerVisible(markerID)) { // 태양 마커 detecting 되면
            gl.glMatrixMode(GL10.GL_MODELVIEW);

            Mat_sun = ARToolKit.getInstance().queryMarkerTransformation(markerID);
            gl.glLoadMatrixf(ARToolKit.getInstance().queryMarkerTransformation(markerID), 0); // 태양 마커 기준
            gl.glTranslatef(eyeposition[0], eyeposition[1], -eyeposition[2]); // 카메라 위치 설정
            Add_Light(gl, SUN_LIGHT, light_ambient, light_diffuse, light_specular);
            sun_emission[0]=1.0f; sun_emission[1]=1.0f; sun_emission[2]=1.0f;
            gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, makeFloatBuffer(sun_emission));

            angle_sun_rotation+=rotation_sun%360;
            gl.glRotatef(angle_sun_rotation, 0.0f, 0.0f, 1.0f); // 태양 자전, 방향
            sun.draw(gl); // 조명위치가 태양의 내부가 되어 태양이 어둡게 표현되기 때문에 Emissive material로 표현
            sun_emission[0]=0.0f; sun_emission[1]=0.0f; sun_emission[2]=0.0f;
            gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, makeFloatBuffer(sun_emission));
            gl.glPushMatrix(); // 현재의 Modeling transform matrix 저장 - 태양

            // 태양 마커 detecting 상태에서 지구 마커 detecting 되었을 때
            if (ARToolKit.getInstance().queryMarkerVisible(markerID1)) {

                Mat_earth = ARToolKit.getInstance().queryMarkerTransformation(markerID1);
                Dist_sun_earth = Mat_earth;
                Matrix.invertM(Mat_sun, 0, Mat_sun, 0); // 역행렬 구하기
                Matrix.multiplyMM(Dist_sun_earth, 0, Mat_sun, 0, Mat_earth, 0); // 태양 & 지구 사이의 matrix 관계

                angle_earth_rotation += rotation_earth % 360;
                gl.glRotatef(angle_earth_rotation, 0.0f, 0.0f, 1.0f); // 지구 자전, 방향
                gl.glTranslatef(Dist_sun_earth[12], Dist_sun_earth[13], Dist_sun_earth[14]); // 태양 & 지구 사이 거리
                angle_earth += revolution_earth;
                gl.glRotatef(angle_earth, 0.0f, 0f, 1.0f); // 지구 공전, 방향
                earth.draw(gl);

                // 태양 마커 detecting 상태에서 지구 마커 detecting 되고 달 마커 detecting 되었을 때
                if (ARToolKit.getInstance().queryMarkerVisible(markerID2)) {

                    Mat_earth_r = ARToolKit.getInstance().queryMarkerTransformation(markerID1);
                    Mat_moon = ARToolKit.getInstance().queryMarkerTransformation(markerID2);

                    Dist_earth_moon = Mat_moon;
                    Matrix.invertM(Mat_earth_r, 0, Mat_earth_r, 0); // 역행렬 구하기
                    Matrix.multiplyMM(Dist_earth_moon, 0, Mat_earth_r, 0, Mat_moon, 0); // 지구 & 달 사이의 matrix 관계

                    angle_moon_rotation += rotation_moon % 360;
                    gl.glRotatef(angle_moon_rotation, 0.0f, 0.0f, 0.6f); // 달 자전, 방향

                    gl.glTranslatef(Dist_earth_moon[12], Dist_earth_moon[13], Dist_earth_moon[14]); // 지구 & 달 사이 거리
                    angle_moon += revolution_moon % 360;
                    gl.glRotatef(angle_moon, 0.0f, 0.0f, 1.0f); // 지구에 대한 달의 공전 주기, 방향
                    moon.draw(gl);
                }
                gl.glPopMatrix();
            }
            // 태양 마커 detecting 상태에서 목성 마커 detecting 되었을 때
            if (ARToolKit.getInstance().queryMarkerVisible(markerID3)) {
                Mat_sun_r = ARToolKit.getInstance().queryMarkerTransformation(markerID);
                gl.glLoadMatrixf(ARToolKit.getInstance().queryMarkerTransformation(markerID), 0); // 태양 마커 기준
                Mat_jupiter = ARToolKit.getInstance().queryMarkerTransformation(markerID3);
                Dist_sun_jupiter = Mat_jupiter;
                Matrix.invertM(Mat_sun_r, 0, Mat_sun_r, 0); // 역행렬 구하기
                Matrix.multiplyMM(Dist_sun_jupiter, 0, Mat_sun_r, 0, Mat_jupiter, 0); // 태양 & 목성 사이의 matrix 관계

                angle_jupiter_rotation += rotation_jupiter % 360;
                gl.glRotatef(angle_jupiter_rotation, 0.0f, 0.0f, 1.0f); // 목성 자전, 방향
                gl.glTranslatef(Dist_sun_jupiter[12], Dist_sun_jupiter[13], Dist_sun_jupiter[14]); // 태양 & 목성 사이 거리
                angle_jupiter += revolution_jupiter;
                gl.glRotatef(angle_jupiter, 0.0f, 0f, 1.0f); // 목성 공전, 방향
                jupiter.draw(gl);
            }
        }
    }
}