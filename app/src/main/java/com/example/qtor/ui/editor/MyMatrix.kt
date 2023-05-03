package com.example.qtor.ui.editor

import android.graphics.ColorMatrix


class ImageMatrix {
    var m = FloatArray(4 * 5)
    var mColorMatrix = ColorMatrix()
    var mTmpColorMatrix = ColorMatrix()
    var mBrightness = 1f
    var mSaturation = 1f
    var mContrast = 1f
    var mWarmth = 1f

    init {
        updateMatrix()
    }

    private fun saturation(saturationStrength: Float) {
        val Rf = 0.2999f
        val Gf = 0.587f
        val Bf = 0.114f
        val MS = 1.0f - saturationStrength
        val Rt = Rf * MS
        val Gt = Gf * MS
        val Bt = Bf * MS
        m[0] = Rt + saturationStrength
        m[1] = Gt
        m[2] = Bt
        m[3] = 0f
        m[4] = 0f
        m[5] = Rt
        m[6] = Gt + saturationStrength
        m[7] = Bt
        m[8] = 0f
        m[9] = 0f
        m[10] = Rt
        m[11] = Gt
        m[12] = Bt + saturationStrength
        m[13] = 0f
        m[14] = 0f
        m[15] = 0f
        m[16] = 0f
        m[17] = 0f
        m[18] = 1f
        m[19] = 0f
    }

    private fun warmth(warmtha: Float) {
        var warmth = warmtha
        val baseTemperature = 5000f
        if (warmth <= 0) warmth = .01f
        var tmpColor_r: Float
        var tmpColor_g: Float
        var tmpColor_b: Float
        var kelvin = baseTemperature / warmth
        run {
            // simulate a black body radiation
            val centiKelvin = kelvin / 100
            val colorR: Float
            val colorG: Float
            val colorB: Float
            if (centiKelvin > 66) {
                val tmp = centiKelvin - 60f
                colorR = 329.698727446f * Math.pow(tmp.toDouble(), -0.1332047592).toFloat()
                colorG = 288.1221695283f * Math.pow(tmp.toDouble(), 0.0755148492).toFloat()
            } else {
                colorG = 99.4708025861f * Math.log(centiKelvin.toDouble())
                    .toFloat() - 161.1195681661f
                colorR = 255f
            }
            colorB = if (centiKelvin < 66) {
                if (centiKelvin > 19) {
                    138.5177312231f * Math.log((centiKelvin - 10).toDouble())
                        .toFloat() - 305.0447927307f
                } else {
                    0f
                }
            } else {
                255f
            }
            tmpColor_r = Math.min(255f, Math.max(colorR, 0f))
            tmpColor_g = Math.min(255f, Math.max(colorG, 0f))
            tmpColor_b = Math.min(255f, Math.max(colorB, 0f))
        }
        var color_r = tmpColor_r
        var color_g = tmpColor_g
        var color_b = tmpColor_b
        kelvin = baseTemperature
        run {
            // simulate a black body radiation
            val centiKelvin = kelvin / 100
            val colorR: Float
            val colorG: Float
            val colorB: Float
            if (centiKelvin > 66) {
                val tmp = centiKelvin - 60f
                colorR = 329.698727446f * Math.pow(tmp.toDouble(), -0.1332047592).toFloat()
                colorG = 288.1221695283f * Math.pow(tmp.toDouble(), 0.0755148492).toFloat()
            } else {
                colorG = 99.4708025861f * Math.log(centiKelvin.toDouble())
                    .toFloat() - 161.1195681661f
                colorR = 255f
            }
            colorB = if (centiKelvin < 66) {
                if (centiKelvin > 19) {
                    138.5177312231f * Math.log((centiKelvin - 10).toDouble())
                        .toFloat() - 305.0447927307f
                } else {
                    0f
                }
            } else {
                255f
            }
            tmpColor_r = Math.min(255f, Math.max(colorR, 0f))
            tmpColor_g = Math.min(255f, Math.max(colorG, 0f))
            tmpColor_b = Math.min(255f, Math.max(colorB, 0f))
        }
        color_r /= tmpColor_r
        color_g /= tmpColor_g
        color_b /= tmpColor_b
        m[0] = color_r
        m[1] = 0f
        m[2] = 0f
        m[3] = 0f
        m[4] = 0f
        m[5] = 0f
        m[6] = color_g
        m[7] = 0f
        m[8] = 0f
        m[9] = 0f
        m[10] = 0f
        m[11] = 0f
        m[12] = color_b
        m[13] = 0f
        m[14] = 0f
        m[15] = 0f
        m[16] = 0f
        m[17] = 0f
        m[18] = 1f
        m[19] = 0f
    }

    private fun brightness(brightness: Float) {
        m[0] = brightness
        m[1] = 0f
        m[2] = 0f
        m[3] = 0f
        m[4] = 0f
        m[5] = 0f
        m[6] = brightness
        m[7] = 0f
        m[8] = 0f
        m[9] = 0f
        m[10] = 0f
        m[11] = 0f
        m[12] = brightness
        m[13] = 0f
        m[14] = 0f
        m[15] = 0f
        m[16] = 0f
        m[17] = 0f
        m[18] = 1f
        m[19] = 0f
    }

    fun updateMatrix() {
        mColorMatrix.reset()
        if (mSaturation != 1.0f) {
            saturation(mSaturation)
            mColorMatrix.set(m)
        }
        if (mContrast != 1.0f) {
            mTmpColorMatrix.setScale(mContrast, mContrast, mContrast, 1f)
            mColorMatrix.postConcat(mTmpColorMatrix)
        }
        if (mWarmth != 1.0f) {
            warmth(mWarmth)
            mTmpColorMatrix.set(m)
            mColorMatrix.postConcat(mTmpColorMatrix)
        }
        if (mBrightness != 1.0f) {
            brightness(mBrightness)
            mTmpColorMatrix.set(m)
            mColorMatrix.postConcat(mTmpColorMatrix)
        }
    }
}