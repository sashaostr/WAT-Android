/*
Web App Template for Android 

Copyright (c) Microsoft Corporation

All rights reserved. 

MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the ""Software""), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

package com.smartbee.wat.Helper;

import android.content.Context;
import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by mdotson on 6/5/2014.
 */
public class Assets {
    public static String readEncoded(String fileName, Context context) {
        InputStream ins = null;
        try {
            ins = context.getAssets().open(fileName);

            int size = ins.available();
            byte[] buffer = new byte[size];
            ins.read(buffer);
            ins.close();
            ins = null;

            int offset = 0;
            byte[] BOM = new byte[] { (byte)239, (byte)187, (byte)191 };
            if (buffer.length >= 3 && buffer[0] == BOM[0] && buffer[1] == BOM[1] && buffer[2] == BOM[2]) {
                offset = 3;
            }

            return Base64.encodeToString(buffer, offset, size - offset, Base64.NO_WRAP);
        } catch (IOException ioe) {
            return null;
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static String readText(String fileName, Context context) {
        InputStream ins = null;
        try {
            ins = context.getAssets().open(fileName);
            InputStreamReader reader = new InputStreamReader(ins);

            int size = ins.available();
            byte[] buffer = new byte[size];
            ins.read(buffer);
            ins.close();
            ins = null;

            return new String(buffer, reader.getEncoding());
        }
        catch(IOException ioe) {
            return null;
        }
        finally {
            if(ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}