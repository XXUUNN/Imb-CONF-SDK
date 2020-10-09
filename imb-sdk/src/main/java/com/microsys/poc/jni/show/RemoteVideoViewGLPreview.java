package com.microsys.poc.jni.show;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class RemoteVideoViewGLPreview extends GLSurfaceView implements GLSurfaceView.Renderer {
    private static final String TAG = RemoteVideoViewGLPreview.class.getSimpleName();
    int mBufferWidthY, mBufferHeightY, mBufferWidthUV, mBufferHeightUV;

    int mBufferPositionY, mBufferPositionU, mBufferPositionV;

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int SHORT_SIZE_BYTES = 2;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;

    private static final float[] TRIANFLE_VERTICES_DATA_H = {
            1, -1, 0, 1, 0,
            1, 1, 0, 0, 0,
            -1, 1, 0, 0, 1,
            -1, -1, 0, 1, 1
    };

    private static final float[] TRIANFLE_VERTICES_B_L_L = {
            1, -1, 0, 1, 1,
            1, 1, 0, 1, 0,
            -1, 1, 0, 0, 0,
            -1, -1, 0, 0, 1
    };

    private static final float[] TRIANFLE_VERTICES_IOS_B_L_L = {
            1, 1, 0, 1, 0,
            -1, 1, 0, 0, 0,
            -1, -1, 0, 0, 1,
            1, -1, 0, 1, 1
    };

    private static final float[] TRIANFLE_VERTICES_IOS_B_P_P = {
            1, -1, 0, 1, 0,
            1, 1, 0, 0, 0,
            -1, 1, 0, 0, 1,
            -1, -1, 0, 1, 1
    };
    private static final float[] TRIANFLE_VERTICES_IOS_B_P_L = {
            1, 1, 0, 0, 0,
            -1, 1, 0, 0, 1,
            -1, -1, 0, 1, 1,
            1, -1, 0, 1, 0
    };


    private static final float[] TRIANFLE_VERTICES_B_P_L = {
            1, -1, 0, 1, 0,
            1, 1, 0, 0, 0,
            -1, 1, 0, 0, 1,
            -1, -1, 0, 1, 1
    };

    private static final float[] TRIANFLE_VERTICES_B_L_P = {
            1, 1, 0, 1, 0,
            -1, 1, 0, 0, 0,
            -1, -1, 0, 0, 1,
            1, -1, 0, 1, 1
    };

    private static final float[] TRIANFLE_VERTICES_IOS_B_L_P = {
            1, 1, 0, 1, 0,
            -1, 1, 0, 0, 0,
            -1, -1, 0, 0, 1,
            1, -1, 0, 1, 1
    };

    private static final float[] TRIANFLE_VERTICES_DATA_V = {
            -1, -1, 0, 0, 1,
            -1, 1, 0, 1, 1,
            1, 1, 0, 1, 0,
            1, -1, 0, 0, 0
    };
    private static final float[] TRIANFLE_VERTICES_IOS_F_L_L = {
            -1, 1, 0, 0, 1,
            1, 1, 0, 1, 1,
            1, -1, 0, 1, 0,
            -1, -1, 0, 0, 0
    };

    private static final float[] TRIANFLE_VERTICES_IOS_F_P_L = {
            1, -1, 0, 1, 1,
            -1, -1, 0, 1, 0,
            -1, 1, 0, 0, 0,
            1, 1, 0, 0, 1
    };


    private static final float[] TRIANFLE_VERTICES_IOS_F_P_P = {
            -1, 1, 0, 0, 0,
            1, 1, 0, 0, 1,
            1, -1, 0, 1, 1,
            -1, -1, 0, 1, 0
    };

    private static final float[] TRIANFLE_VERTICES_PAD_S_L_L = {
            -1, -1, 0, 0, 1,
            1, -1, 0, 1, 1,
            1, 1, 0, 1, 0,
            -1, 1, 0, 0, 0
    };

    private static final float[] TRIANFLE_VERTICES_PAD_F_L_L = {
            1, -1, 0, 0, 1,
            -1, -1, 0, 1, 1,
            -1, 1, 0, 1, 0,
            1, 1, 0, 0, 0
    };


    private static final float[] TRIANFLE_VERTICES_PAD_B_L_L = {
            -1, -1, 0, 0, 1,
            1, -1, 0, 1, 1,
            1, 1, 0, 1, 0,
            -1, 1, 0, 0, 0
    };

    private static final float[] TRIANFLE_VERTICES_F_P_L = {
            -1, -1, 0, 0, 1,
            -1, 1, 0, 1, 1,
            1, 1, 0, 1, 0,
            1, -1, 0, 0, 0
    };

    private final float[] TRIANFLE_VERTICES_F_L_P = {
            -1, -1, 0, 1, 1,
            -1, 1, 0, 1, 0,
            1, 1, 0, 0, 0,
            1, -1, 0, 0, 1,
    };

    private static final float[] TRIANFLE_VERTICES_F_L_L = {
            1, 1, 0, 0, 0,
            1, -1, 0, 0, 1,
            -1, -1, 0, 1, 1,
            -1, 1, 0, 1, 0
    };

    private static final float[] TRIANFLE_VERTICES_IOS_F_L_P = {
            1, 1, 0, 1, 1,
            1, -1, 0, 1, 0,
            -1, -1, 0, 0, 0,
            -1, 1, 0, 0, 1,
    };

    private static final float[] TRIANFLE_VERTICES_PAD_S_L_P = {
            -1, -1, 0, 0, 1,
            -1, 1, 0, 0, 0,
            1, 1, 0, 1, 0,
            1, -1, 0, 1, 1,
    };

    private static final float[] TRIANFLE_VERTICES_PAD_F_L_P = {
            -1, -1, 0, 1, 1,
            -1, 1, 0, 1, 0,
            1, 1, 0, 0, 0,
            1, -1, 0, 0, 1,
    };

    private static final float[] TRIANFLE_VERTICES_PAD_B_L_P = {
            1, -1, 0, 1, 1,
            1, 1, 0, 1, 0,
            -1, 1, 0, 0, 0,
            -1, -1, 0, 0, 1,
    };

    private static final float[] TRIANFLE_VERTICES_DATA_F = {
            -1, -1, 0, 0, 0,
            -1, 1, 0, 1, 0,
            1, 1, 0, 1, 1,
            1, -1, 0, 0, 1
    };

    /*******reverse  android  start**********/
    private static final float[] TRIANFLE_VERTICES_B_RL_P = {
            -1, -1, 0, 1, 0,
            1, -1, 0, 0, 0,
            1, 1, 0, 0, 1,
            -1, 1, 0, 1, 1
    };

    private static final float[] TRIANFLE_VERTICES_B_RL_L = {
            -1, 1, 0, 1, 1,
            -1, -1, 0, 1, 0,
            1, -1, 0, 0, 0,
            1, 1, 0, 0, 1
    };

    private static final float[] TRIANFLE_VERTICES_F_RL_P = {
            1, 1, 0, 1, 1,
            1, -1, 0, 1, 0,
            -1, -1, 0, 0, 0,
            -1, 1, 0, 0, 1,
    };

    private static final float[] TRIANFLE_VERTICES_F_RL_L = {
            -1, -1, 0, 0, 0,
            -1, 1, 0, 0, 1,
            1, 1, 0, 1, 1,
            1, -1, 0, 1, 0
    };
    /*******reverse  android  end**********/


    private static final short[] INDICES_DATA = {
            0, 1, 2,
            2, 3, 0};

    private FloatBuffer mTriangleVertices;
    private ShortBuffer mIndices;

    private static final String VERTEX_SHADER_SOURCE =
            "attribute vec4 aPosition;\n" +
                    "attribute vec2 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = aPosition;\n" +
                    "  vTextureCoord = aTextureCoord;\n" +
                    "}\n";

    private static final String FRAGMENT_SHADER_SOURCE = "precision mediump float;" +
            "varying vec2 vTextureCoord;" +
            "" +
            "uniform sampler2D SamplerY; " +
            "uniform sampler2D SamplerU;" +
            "uniform sampler2D SamplerV;" +
            "" +
            "const mat3 yuv2rgb = mat3(1, 0, 1.2802,1, -0.214821, -0.380589,1, 2.127982, 0);" +
            "" +
            "void main() {    " +
            "    vec3 yuv = vec3(1.1643 * (texture2D(SamplerY, vTextureCoord).r - 0.0625)," +
            "                    texture2D(SamplerU, vTextureCoord).r - 0.5," +
            "                    texture2D(SamplerV, vTextureCoord).r - 0.5);" +
            "    vec3 rgb = yuv * yuv2rgb;    " +
            "    gl_FragColor = vec4(rgb, 1.0);" +
            "} ";

    private int mProgram;
    private int maPositionHandle;
    private int maTextureHandle;
    private int muSamplerYHandle;
    private int muSamplerUHandle;
    private int muSamplerVHandle;
    private int[] mTextureY = new int[1];
    private int[] mTextureU = new int[1];
    private int[] mTextureV = new int[1];

    boolean mSurfaceDestroyed, mSurfaceCreated;

    private int surfaceViewW, surfaceViewH;
    int preBufferW = -1, preBufferH = -1;
    private int preDirection = -1;

    private ByteBuffer mVideoFrame;

    private boolean isBlackScreen;
    private int id;

    private IRemoteDrawCalc.Params drawParams;
    private IRemoteDrawCalc remoteDrawCalc;

    @SuppressWarnings("deprecation")
    public RemoteVideoViewGLPreview(Context context, ByteBuffer buffer,
                                    int bufferWidth, int bufferHeight,
                                    int previewMaxW, int previewMaxH, int id) {
        super(context);

        this.id = id;
        surfaceViewW = previewMaxW;
        surfaceViewH = previewMaxH;

        drawParams = new IRemoteDrawCalc.Params(surfaceViewW, surfaceViewH, 0,
                0, 0, 0, 0, 0, 0, 0);

        mTriangleVertices = ByteBuffer.allocateDirect(TRIANFLE_VERTICES_DATA_F.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();

        mIndices = ByteBuffer.allocateDirect(INDICES_DATA.length
                * SHORT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndices.put(INDICES_DATA).position(0);

        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        setRenderer(this);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_GPU);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        setBuffer(buffer, bufferWidth, bufferHeight, 0);
    }

    public void setDrawCalc(IRemoteDrawCalc calc) {
        remoteDrawCalc = calc;
    }

    public void setBuffer(ByteBuffer buffer, int bufferWidth, int bufferHeight, int direction) {
        mVideoFrame = buffer;

        mBufferWidthY = bufferWidth;
        mBufferHeightY = bufferHeight;

        mBufferWidthUV = (mBufferWidthY >> 1);
        mBufferHeightUV = (mBufferHeightY >> 1);

        mBufferPositionY = 1;
        mBufferPositionU = (mBufferWidthY * mBufferHeightY) + 1;
        mBufferPositionV = mBufferPositionU + (mBufferWidthUV * mBufferHeightUV);

        drawParams.videoDir = direction;

        drawParams.videoW = mBufferWidthY;
        drawParams.videoH = mBufferHeightY;
    }

    public boolean isDestroyed() {
        return mSurfaceDestroyed;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        mSurfaceCreated = true;
        mSurfaceDestroyed = false;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceDestroyed = true;
        mSurfaceCreated = false;
        super.surfaceDestroyed(holder);
    }

//    IRemoteDrawCalc.Params lockedParams;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (remoteDrawCalc != null) {
//                if (lockedOrientation>-1) {
//                    //如果锁定了方向 就用锁定的方向计算 不改变画的参数 保持画面不变化
//                    IRemoteDrawCalc.Params params = new IRemoteDrawCalc.Params(drawParams.surfaceW,drawParams.surfaceH,
//                            drawParams.x,drawParams.y,drawParams.w,drawParams.h,
//                            drawParams.getVideoW(),drawParams.getVideoH(),drawParams.getVideoDir(),
//                            lockedOrientation);
//                    Log.i(TAG, "calc handleMessage: 使用了 之前 的方向 "+id);
//                    remoteDrawCalc.calc(params);
//                    lockedParams = params;
//                    //在对面正确的锁屏方向流传过来前的这段时间
//                }else{
//                    remoteDrawCalc.calc(drawParams);
//                    Log.i(TAG, "calc handleMessage: 使用了 当前 的方向 "+id);
//                }
                remoteDrawCalc.calc(drawParams);
            } else {
                Log.e(TAG, "handleMessage: 未实现计算尺寸的方法 remoteViewId=" + id);
            }
        }
    };

    @Override
    public void onDrawFrame(GL10 glUnused) {
//        Log.i(TAG, "id=" + id + "_onDrawFrame: " + preBufferW + "_" + mBufferWidthY);

        if (preBufferW != mBufferWidthY || preBufferH != mBufferHeightY || (drawParams.videoDir != preDirection)) {
            Log.i(TAG, "onDrawFrame: getDrawCalc" + id);
//            if (lockedOrientation > -1 && !VideoDirection.isSameDirection(lockedOrientation,drawParams.videoDir)) {
//                Log.i(TAG, "onDrawFrame: " + "远程方向还未锁定 不刷新界面" +lockedOrientation
//                +"_"+drawParams.videoDir);
//                return;
//            }

            handler.sendEmptyMessage(0);
            preBufferW = mBufferWidthY;
            preBufferH = mBufferHeightY;
            preDirection = drawParams.videoDir;
        }

        synchronized (mVideoFrame) {
            mTriangleVertices.clear();
            if (mVideoFrame.limit() == 0) {
                return;
            }
            byte direction = mVideoFrame.get(0);

            adapterDirection(direction);

//            Log.i(TAG, direction + "_onDrawFrame: " + drawParams.x + "_" + drawParams.y + "," +
//                    drawParams.w + "_" + drawParams.h);

            GLES20.glViewport(drawParams.x, drawParams.y, drawParams.w, drawParams.h);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            GLES20.glUseProgram(mProgram);
            checkGlError("glUseProgram");

            //没数据就不画
            if (isBlackScreen || mVideoFrame.limit() == 1) {
                isBlackScreen = false;
            } else {
                dealVideoBuffer(mVideoFrame);
            }
//            if (!isBlackScreen ) {
//                dealVideoBuffer(mVideoFrame);
//            } else {
//                isBlackScreen = false;
//            }
        }
    }

    private void adapterDirection(int direction) {
//        Log.i(TAG, "adapterDirection: " + direction);
        int screenDir = drawParams.screenDir;
        if (lockedOrientation > -1) {
            //锁定了屏幕
            if (VideoDirection.isSameDirection(lockedOrientation, direction)) {
                //方向相同的话 就使用 锁定的方向
                screenDir = lockedOrientation;
            } else {
                //否则就先使用之前的方向保持画面不变化
            }
        }
        switch (direction) {
            case VideoDirection.ANDROID_BACK_PORTRAIT:
                if (screenDir == VideoDirection.SCREEN_PORTRAIT) {
                    mTriangleVertices.put(TRIANFLE_VERTICES_DATA_H).position(0);
                } else {
                    mTriangleVertices.put(TRIANFLE_VERTICES_B_P_L).position(0);
                }
                break;
            case VideoDirection.ANDROID_FRONT_PORTRAIT:
                if (screenDir == VideoDirection.SCREEN_PORTRAIT) {
                    mTriangleVertices.put(TRIANFLE_VERTICES_DATA_V).position(0);
                } else {
                    mTriangleVertices.put(TRIANFLE_VERTICES_F_P_L).position(0);
                }
                break;
            case VideoDirection.ANDROID_BACK_LANDSCAPE:
                if (screenDir == VideoDirection.SCREEN_PORTRAIT) {
                    mTriangleVertices.put(TRIANFLE_VERTICES_B_L_P).position(0);
                } else {
                    mTriangleVertices.put(TRIANFLE_VERTICES_B_L_L).position(0);
                }
                break;
            case VideoDirection.ANDROID_FRONT_LANDSCAPE:
                if (screenDir == VideoDirection.SCREEN_PORTRAIT) {
                    mTriangleVertices.put(TRIANFLE_VERTICES_F_L_P).position(0);
                } else {
                    mTriangleVertices.put(TRIANFLE_VERTICES_F_L_L).position(0);
                }
                break;
            case VideoDirection.ANDROID_PAD_BACK:
                if (screenDir == VideoDirection.SCREEN_PORTRAIT) {
                    mTriangleVertices.put(TRIANFLE_VERTICES_PAD_B_L_P).position(0);
                } else {
                    mTriangleVertices.put(TRIANFLE_VERTICES_PAD_B_L_L).position(0);
                }
                break;
            case VideoDirection.ANDROID_PAD_FRONT:
                if (screenDir == VideoDirection.SCREEN_PORTRAIT) {
                    mTriangleVertices.put(TRIANFLE_VERTICES_PAD_F_L_P).position(0);
                } else {
                    mTriangleVertices.put(TRIANFLE_VERTICES_PAD_F_L_L).position(0);
                }
                break;
            case VideoDirection.ANDROID_PAD_SCREEN_RECORD:
                if (screenDir == VideoDirection.SCREEN_PORTRAIT) {
                    mTriangleVertices.put(TRIANFLE_VERTICES_PAD_S_L_P).position(0);
                } else {
                    mTriangleVertices.put(TRIANFLE_VERTICES_PAD_S_L_L).position(0);
                }
                break;
            case VideoDirection.ANDROID_TV_BACK:
                if (screenDir == VideoDirection.SCREEN_PORTRAIT) {
                    mTriangleVertices.put(TRIANFLE_VERTICES_PAD_B_L_P).position(0);
                } else {
                    mTriangleVertices.put(TRIANFLE_VERTICES_PAD_B_L_L).position(0);
                }
                break;
            case VideoDirection.ANDROID_TV_FRONT:
                if (screenDir == VideoDirection.SCREEN_PORTRAIT) {
                    mTriangleVertices.put(TRIANFLE_VERTICES_PAD_F_L_P).position(0);
                } else {
                    mTriangleVertices.put(TRIANFLE_VERTICES_PAD_F_L_L).position(0);
                }
                break;
            case VideoDirection.ANDROID_TV_SCREEN_RECORD:
                if (screenDir == VideoDirection.SCREEN_PORTRAIT) {
                    mTriangleVertices.put(TRIANFLE_VERTICES_PAD_S_L_P).position(0);
                } else {
                    mTriangleVertices.put(TRIANFLE_VERTICES_PAD_S_L_L).position(0);
                }
                break;
            case VideoDirection.IOS_BACK_PORTRAIT:
                if (screenDir == VideoDirection.SCREEN_PORTRAIT) {
                    mTriangleVertices.put(TRIANFLE_VERTICES_IOS_B_P_P).position(0);
                } else {
                    mTriangleVertices.put(TRIANFLE_VERTICES_IOS_B_P_L).position(0);
                }
                break;
            case VideoDirection.IOS_FRONT_PORTRAIT:
                if (screenDir == VideoDirection.SCREEN_PORTRAIT) {
                    mTriangleVertices.put(TRIANFLE_VERTICES_IOS_F_P_P).position(0);
                } else {
                    mTriangleVertices.put(TRIANFLE_VERTICES_IOS_F_P_L).position(0);
                }
                break;
            case VideoDirection.IOS_BACK_LANDSCAPE:
                if (screenDir == VideoDirection.SCREEN_PORTRAIT) {
                    mTriangleVertices.put(TRIANFLE_VERTICES_IOS_B_L_P).position(0);
                } else {
                    mTriangleVertices.put(TRIANFLE_VERTICES_IOS_B_L_L).position(0);
                }
                break;
            case VideoDirection.IOS_FRONT_LANDSCAPE:
                if (screenDir == VideoDirection.SCREEN_PORTRAIT) {
                    mTriangleVertices.put(TRIANFLE_VERTICES_IOS_F_L_P).position(0);
                } else {
                    mTriangleVertices.put(TRIANFLE_VERTICES_IOS_F_L_L).position(0);
                }
                break;
            case VideoDirection.ANDROID_BACK_REVERSE_LANDSCAPE:
                if (screenDir == VideoDirection.SCREEN_PORTRAIT) {
                    mTriangleVertices.put(TRIANFLE_VERTICES_B_RL_P).position(0);
                } else {
                    mTriangleVertices.put(TRIANFLE_VERTICES_B_RL_L).position(0);
                }
                break;
            case VideoDirection.ANDROID_FRONT_REVERSE_LANDSCAPE:
                if (screenDir == VideoDirection.SCREEN_PORTRAIT) {
                    mTriangleVertices.put(TRIANFLE_VERTICES_F_RL_P).position(0);
                } else {
                    mTriangleVertices.put(TRIANFLE_VERTICES_F_RL_L).position(0);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        Log.i(TAG, "onSurfaceChanged: " + surfaceViewW + "_" + surfaceViewH);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_DITHER);
        GLES20.glDisable(GLES20.GL_STENCIL_TEST);
        GLES20.glDisable(GL10.GL_DITHER);

        String extensions = GLES20.glGetString(GL10.GL_EXTENSIONS);
        Log.d(TAG, "OpenGL extensions=" + extensions);

        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        mProgram = createProgram(VERTEX_SHADER_SOURCE, FRAGMENT_SHADER_SOURCE);
        if (mProgram == 0) {
            return;
        }
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        checkGlError("glGetAttribLocation aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        checkGlError("glGetAttribLocation aTextureCoord");
        if (maTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }

        muSamplerYHandle = GLES20.glGetUniformLocation(mProgram, "SamplerY");
        if (muSamplerYHandle == -1) {
            throw new RuntimeException("Could not get uniform location for SamplerY");
        }
        muSamplerUHandle = GLES20.glGetUniformLocation(mProgram, "SamplerU");
        if (muSamplerUHandle == -1) {
            throw new RuntimeException("Could not get uniform location for SamplerU");
        }
        muSamplerVHandle = GLES20.glGetUniformLocation(mProgram, "SamplerV");
        if (muSamplerVHandle == -1) {
            throw new RuntimeException("Could not get uniform location for SamplerV");
        }

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        checkGlError("glVertexAttribPointer maPosition");

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        checkGlError("glEnableVertexAttribArray maPositionHandle");
        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        checkGlError("glVertexAttribPointer maTextureHandle");
        GLES20.glEnableVertexAttribArray(maTextureHandle);
        checkGlError("glEnableVertexAttribArray maTextureHandle");

        GLES20.glGenTextures(1, mTextureY, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureY[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glGenTextures(1, mTextureU, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureU[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glGenTextures(1, mTextureV, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureV[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        Log.i(TAG, "onSurfaceCreated: " + surfaceViewW + "_" + surfaceViewH);
    }

    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    private int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    private void dealVideoBuffer(ByteBuffer mBuffer) {
        if (mBuffer != null) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureY[0]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0,
                    GLES20.GL_LUMINANCE, mBufferWidthY, mBufferHeightY,
                    0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE,
                    mBuffer.position(mBufferPositionY));
            GLES20.glUniform1i(muSamplerYHandle, 0);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureU[0]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0,
                    GLES20.GL_LUMINANCE, mBufferWidthUV, mBufferHeightUV,
                    0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE,
                    mBuffer.position(mBufferPositionU));

            GLES20.glUniform1i(muSamplerUHandle, 1);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureV[0]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0,
                    GLES20.GL_LUMINANCE, mBufferWidthUV, mBufferHeightUV,
                    0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE,
                    mBuffer.position(mBufferPositionV));

            GLES20.glUniform1i(muSamplerVHandle, 2);
        }
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, INDICES_DATA.length,
                GLES20.GL_UNSIGNED_SHORT, mIndices);
        mBuffer.position(0);
    }

    public void setScreenOrientation(int screenOrientation) {
        this.drawParams.screenDir = screenOrientation;
    }

    public void setBlackScreen(boolean blackScreen) {
        isBlackScreen = blackScreen;
    }

    /**
     * 锁定的方向
     * -1 没有锁定
     * >-1 锁定了的方向
     */
    private int lockedOrientation = -1;

    public void setScreenOrientationLocked(int lockedOrientation) {
        this.lockedOrientation = lockedOrientation;
        if (lockedOrientation > -1) {
            drawParams.screenDir = lockedOrientation;
        }
    }
}