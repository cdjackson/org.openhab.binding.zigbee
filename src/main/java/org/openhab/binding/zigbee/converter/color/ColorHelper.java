/**
 * Copyright (c) 2014-2017 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.zigbee.converter.color;

/**
 *
 * @author Pedro Garcia - Initial Contribution
 *
 */
public class ColorHelper {
    // 1931 CIE XYZ to sRGB (D65 reference white)
    protected static float Xy2Rgb[][] = {
        {  3.2406f, -1.5372f, -0.4986f },
        { -0.9689f,  1.8758f,  0.0415f },
        {  0.0557f, -0.2040f,  1.0570f }
    };
    
    // sRGB to 1931 CIE XYZ (D65 reference white)
    protected static float Rgb2Xy[][] = {
        {  0.4124f,  0.3576f,  0.1805f },
        {  0.2126f,  0.7152f,  0.0722f },
        {  0.0193f,  0.1192f,  0.9505f }
    };

    protected static float gammaCompress(float c) {
        if(c < 0.0f) c = 0.0f;
        else if(c > 1.0f) c = 1.0f;

        return c <= 0.0031308f ? 19.92f * c : (1.0f + 0.055f) * (float) Math.pow(c, 1.0f/2.4f) - 0.055f;
	// return c;
    }

    protected static float gammaDecompress(float c) {
        if(c < 0.0f) c = 0.0f;
        else if(c > 1.0f) c = 1.0f;

        return c <= 0.04045f ? c / 19.92f : (float) Math.pow((c + 0.055f) / (1.0f + 0.055f), 2.4f);
	// return c;
    }

    protected static float min(float a, float b, float c) {
        return a < b ? a < c ? a : c : b < c ? b : c ;
    }

    protected static float max(float a, float b, float c) {
        return a > b ? a > c ? a : c : b > c ? b : c ;
    }

    public static float[] xy2rgb(float x, float y) {
    
        float Y = 1.0f;
        float X = (Y / y) * x;
        float Z = (Y / y) * (1.0f - x - y);  
    
        float r = X * Xy2Rgb[0][0] + Y * Xy2Rgb[0][1] + Z * Xy2Rgb[0][2];
        float g = X * Xy2Rgb[1][0] + Y * Xy2Rgb[1][1] + Z * Xy2Rgb[1][2];
        float b = X * Xy2Rgb[2][0] + Y * Xy2Rgb[2][1] + Z * Xy2Rgb[2][2];

        r = gammaCompress(r);    
        g = gammaCompress(g);    
        b = gammaCompress(b);    
        float m = max(r, g, b);

        return new float[] {r/m, g/m, b/m};
    }

    public static float[] rgb2xy(float r, float g, float b) {
    
        r = gammaDecompress(r);    
        g = gammaDecompress(g);    
        b = gammaDecompress(b);

        float X = r * Rgb2Xy[0][0] + g * Rgb2Xy[0][1] + b * Rgb2Xy[0][2];
        float Y = r * Rgb2Xy[1][0] + g * Rgb2Xy[1][1] + b * Rgb2Xy[1][2];
        float Z = r * Rgb2Xy[2][0] + g * Rgb2Xy[2][1] + b * Rgb2Xy[2][2];
        
        float x = X / (X + Y + Z);
        float y = Y / (X + Y + Z);
        
        return new float[] {x, y};
    }

    public static float[] hsv2rgb(float h, float s, float v) {
            
        float r;
        float g;
        float b;
            
        if (s == 0.0f) {
            r = 1.0f;
            g = 1.0f;
            b = 1.0f;
        }
        else {
            int region = (int)(6.0f * h);
            float remainder = 6.0f * h - region;

            float p = 1.0f - s;
            float q = 1.0f - s * remainder;
            float t = 1.0f - s * (1.0f - remainder);
    
            if(region == 0) {
                r = 1.0f;
                g = t;
                b = p;
            }
            else if(region == 1) {
                r = q;
                g = 1.0f;
                b = p;
            }
            else if(region == 2) {
                r = p;
                g = 1.0f;
                b = t;
            }
            else if(region == 3) {
                r = p;
                g = q;
                b = 1.0f;
            }
            else if(region == 4) {
                r = t;
                g = p;
                b = 1.0f;
            }
            else {
                r = 1.0f;
                g = p;
                b = q;
            }
        }
            
        return new float[] {r*v, g*v, b*v};
    }

    public static float[] rgb2hsv(float r, float g, float b) {
  
        float m = max(r, g, b);
        float delta = m - min(r, g, b);

        float h;
        float s;
        float v = m;

        if (delta == 0.0f) {
            h = 0.0f;
            s = 0.0f;
        }
        else {
            s = delta / m;
            if (r == m) h = ( g - b ) / delta;        // between yellow & magenta
            else if(g == m) h = 2.0f + ( b - r ) / delta;    // between cyan & yellow
            else h = 4.0f + ( r - g ) / delta;        // between magenta & cyan
            h /= 6.0f;

            if(h < 0.0f) h += 1.0f;
        }

        return new float[] {h, s, v};
    }

    public static float[] hsv2xy(float h, float s, float v) {
        float rgb[] = hsv2rgb(h, s, v);
        return rgb2xy(rgb[0], rgb[1], rgb[2]);
    }

    public static float[] xy2hsv(float x, float y) {
        float rgb[] = xy2rgb(x, y);
        return rgb2hsv(rgb[0], rgb[1], rgb[2]);
    }
}

