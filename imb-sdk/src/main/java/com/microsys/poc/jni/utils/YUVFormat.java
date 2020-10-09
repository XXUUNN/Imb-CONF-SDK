package com.microsys.poc.jni.utils;

import android.annotation.SuppressLint;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import com.imb.yuv.YuvUtils;
import com.microsys.poc.jni.entity.type.CameraFormat;

public class YUVFormat {

    //解码特殊输出格式
    public static final int useOMX_QCOM_COLOR_FormatYUV420PackedSemiPlanar32m = 2141391876;
    //自定义输出格式
    public static final int HUAWEI_HN3_U01_NV21 = 123456;
    //记录编码器的型号
    public static String ENCODERNAME = "";


    /**
     * 根据输出格式来选择相对应的方法
     *
     * @param data
     * @param length
     * @param width
     * @param height
     * @param firstFormat
     * @param lastFormat
     * @return
     */
    public static int swapNV21ToOther(byte[] inputData, int length, byte[] outputData, int width, int height, int outputFormat) {


        int len = 0;
        switch (outputFormat) {
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
//                YuvUtils.RotateNV21(inputData,outputData,width,height,90);
//                byte[] temp = Arrays.copyOf(outputData, inputData.length);
//                YuvUtils.NV21ToNV12(temp, outputData, height, width);

                //jni转换
                YuvUtils.NV21ToNV12(inputData, outputData, width, height);
                len = inputData.length;

//                len = swapNV21ToYUV420sp(inputData, length, outputData, width, height);

                break;

            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                len = swapNV21ToYUV420p(inputData, length, outputData, width, height);
                break;
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
            case HUAWEI_HN3_U01_NV21:
                System.arraycopy(inputData, 0, outputData, 0, inputData.length);
                len = inputData.length;
                break;
            default:
                break;
        }

        return len;


    }

    public static int swapNV12AndNV21ToOther(byte[] inputData, int length, CameraFormat inputFormat,
                                             byte[] outputData, int width, int height, int outputFormat) {

        int len = 0;

        if (inputFormat.equals(CameraFormat.NV12)) {//目前就只有海康单兵
            len = swapNV12ToOther(inputData, length, outputData, width, height, outputFormat);
            return len;

        } else {
            len = swapNV21ToOther(inputData, length, outputData, width, height, outputFormat);
            return len;
        }

    }

    /**
     * @param data
     * @param length
     * @param width
     * @param height
     * @param outputFormat
     * @return
     */
    public static int swapNV12ToOther(byte[] inputData, int length, byte[] outputData, int width, int height, int outputFormat) {

        int len = 0;
        switch (outputFormat) {
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
                if (outputData.length < width * height * 3 / 2) {
                    System.out.println("===outputData length==" + outputData.length + "===" + width * height * 3 / 2);
                    return 0;
                }

                len = swapNV12ToNV21(inputData, length, outputData, width, height);
                break;

            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:

                len = swapNV12ToYUV420p(inputData, length, outputData, width, height);
                break;
            default:
                break;
        }

        return len;
    }

    /**
     * 解码时调用
     *
     * @param data
     * @param length
     * @param width
     * @param height
     * @param outputFormat
     * @return
     */
    public static int swapOtherToYUV420p(byte[] inputData, int length, byte[] outputData, int width, int height, int outputFormat, MediaFormat decoderOutputFormat) {

        int len = 0;

        switch (outputFormat) {
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                //不做任何处理
                System.arraycopy(inputData, 0, outputData, 0, length);
                len = length;
                return len;
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYCbYCr:
            case useOMX_QCOM_COLOR_FormatYUV420PackedSemiPlanar32m:
                //jni转换
                YuvUtils.NV12ToI420(inputData,outputData,width,height);
                return length;
//                return swapYUV420SPToYUV420p(inputData, length, outputData, width, height, decoderOutputFormat);

            default:
                return len;
        }
    }

    /**
     * Nv21 转化成 NV12
     *
     * @param data
     * @param length
     * @param width
     * @param height
     * @return
     */
    public static int swapNV21ToYUV420sp(byte[] inputData, int length, byte[] outputData, int width, int height) {


        if (outputData.length < width * height * 3 / 2) {
            LogUtil.getInstance().logWithMethod(new Exception(), "outputData length" + outputData.length + "=" + width * height * 3 / 2, "Zhaolg");
            return 0;
        }

        int len = 0;
        System.arraycopy(inputData, 0, outputData, 0, width * height);

        for (int i = width * height; i < length; i += 2) {
            outputData[i] = inputData[i + 1];
            outputData[i + 1] = inputData[i];
        }

        len = width * height * 3 / 2;

        return len;
    }

    /**
     * Nv12 转化成 NV21
     *
     * @param data
     * @param length
     * @param width
     * @param height
     * @return
     */
    public static int swapNV12ToNV21(byte[] inputData, int length, byte[] outputData, int width, int height) {


        if (outputData.length < width * height * 3 / 2) {
            LogUtil.getInstance().logWithMethod(new Exception(), "outputData length=" + outputData.length + "=" + width * height * 3 / 2, "Zhaolg");
            return 0;
        }

        int len = 0;
        System.arraycopy(inputData, 0, outputData, 0, width * height);

        for (int i = width * height; i < length; i += 2) {
            outputData[i] = inputData[i + 1];
            outputData[i + 1] = inputData[i];
        }

        len = width * height * 3 / 2;

        return len;
    }


    /**
     * NV21转化成I420
     *
     * @param data
     * @param length
     * @param width
     * @param height
     * @return
     */
    public static int swapNV21ToYUV420p(byte[] inputData, int length, byte[] outputData, int width, int height) {


        if (outputData.length < width * height * 3 / 2) {
            LogUtil.getInstance().logWithMethod(new Exception(), "outputData length=" + outputData.length + "=" + width * height * 3 / 2, "Zhaolg");
            return 0;
        }

        int len;
        System.arraycopy(inputData, 0, outputData, 0, width * height);

        int strIndex = width * height;
        //u先取出来，按顺序放好
        for (int i = width * height + 1; i < length; i += 2) {
            outputData[strIndex++] = inputData[i];
        }

        //y也取出来，按顺序放好
        for (int i = width * height; i < length; i += 2) {
            outputData[strIndex++] = inputData[i];
        }

        len = strIndex;

        return len;
    }

    /**
     * NV12转化成I420
     *
     * @param data
     * @param length
     * @param width
     * @param height
     * @return
     */
    public static int swapNV12ToYUV420p(byte[] inputData, int length, byte[] outputData, int width, int height) {

        if (outputData.length < width * height * 3 / 2) {
            LogUtil.getInstance().logWithMethod(new Exception(), "outputData length=" + outputData.length + "=" + width * height * 3 / 2, "Zhaolg");
            return 0;
        }

        int len = 0;

        System.arraycopy(inputData, 0, outputData, 0, width * height);

        int strIndex = width * height;


        for (int i = width * height; i < length; i += 2) {
            outputData[strIndex++] = inputData[i];
        }

        for (int i = width * height + 1; i < length; i += 2) {
            outputData[strIndex++] = inputData[i];
        }

        len = strIndex;


        return len;
    }

    /**
     * YV12转I420
     *
     * @param data
     * @param length
     * @param width
     * @param height
     * @return
     */
    public static int swapYV12ToYUV420p(byte[] inputData, int length, byte[] outputData, int width, int height) {

        if (outputData.length < width * height * 3 / 2) {
            LogUtil.getInstance().logWithMethod(new Exception(), "=outputData length=" + outputData.length + "=" + width * height * 3 / 2, "Zhaolg");
            return 0;
        }

        int len = 0;

        System.arraycopy(inputData, 0, outputData, 0, width * height);

        int strIndex = width * height;


        for (int i = width * height * 5 / 4; i < length; i++) {
            outputData[strIndex++] = inputData[i];
        }

        for (int i = width * height; i < length - width * height / 4; i++) {
            outputData[strIndex++] = inputData[i];
        }

        len = strIndex;


        return len;
    }

    /**
     * @param data
     * @param length
     * @param width
     * @param height
     * @return
     */

    @SuppressLint("NewApi")
    public static int swapYUV420SPToYUV420p(byte[] inputData, int length, byte[] outputData, int width, int height, MediaFormat decoderOutputFormat) {

        if (outputData.length < width * height * 3 / 2) {
            LogUtil.getInstance().logWithMethod(new Exception(), "outputData length=" + outputData.length + "=" + width * height * 3 / 2, "Zhaolg");
            return 0;
        }

        int len = 0;
        int sliceHeight = decoderOutputFormat.getInteger("slice-height");
        int cropTop = decoderOutputFormat.getInteger("crop-top");
        int cropBottom = decoderOutputFormat.getInteger("crop-bottom");
        int cropLeft = decoderOutputFormat.getInteger("crop-left");
        int cropRight = decoderOutputFormat.getInteger("crop-right");
        int stride = decoderOutputFormat.getInteger("stride");


        for (int i = cropTop; i <= cropBottom; i++) {
            System.arraycopy(inputData, i * stride + cropLeft, outputData, width * (i - cropTop), width);
        }

        int ylen = sliceHeight * stride;
        int strIndex = width * height;

        for (int i = cropTop; i <= (cropBottom / 2); i++) {
            for (int j = cropLeft; j <= cropRight; j += 2) {
                outputData[strIndex++] = inputData[ylen + i * stride + j];
            }
        }

        for (int i = cropTop; i <= (cropBottom / 2); i++) {
            for (int j = cropLeft; j <= cropRight; j += 2) {
                outputData[strIndex++] = inputData[ylen + i * stride + j + 1];
            }
        }

        len = strIndex;

        return len;
    }

}