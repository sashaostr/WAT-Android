/*
Web App Template for Android 

Copyright (c) Microsoft Corporation

All rights reserved. 

MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the ""Software""), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

package com.smartbee.wat.Model;

/*
 * Web App Template - JSON Model
 * For more info see
 * http://wat-docs.azurewebsites.net/Json#styles
 */

import java.util.Map;

public class ConfigStyles
{
    private boolean setViewport;
    private String targetWidth;
    private String targetHeight;
    private boolean suppressTouchAction;
    private String extendedSplashScreenBackground;
    private String[] hiddenElements;
    private Map<String, String> backButton;
    private String wrapperCssFile;
    private String customCssFile;
    private String customCssString;

    public ConfigStyles() {}

    // generated Getters and Setters

    public boolean isSetViewport() {
        return setViewport;
    }

    public void setSetViewport(boolean setViewport) {
        this.setViewport = setViewport;
    }

    public String getTargetWidth() {
        return targetWidth;
    }

    public void setTargetWidth(String targetWidth) {
        this.targetWidth = targetWidth;
    }

    public String getTargetHeight() {
        return targetHeight;
    }

    public void setTargetHeight(String targetHeight) {
        this.targetHeight = targetHeight;
    }

    public boolean isSuppressTouchAction() {
        return suppressTouchAction;
    }

    public void setSuppressTouchAction(boolean suppressTouchAction) {
        this.suppressTouchAction = suppressTouchAction;
    }

    public String getExtendedSplashScreenBackground() {
        return extendedSplashScreenBackground;
    }

    public void setExtendedSplashScreenBackground(String extendedSplashScreenBackground) {
        this.extendedSplashScreenBackground = extendedSplashScreenBackground;
    }

    public String[] getHiddenElements() {
        return hiddenElements;
    }

    public void setHiddenElements(String[] hiddenElements) {
        this.hiddenElements = hiddenElements;
    }

    public Map<String, String> getBackButton() {
        return backButton;
    }

    public void setBackButton(Map<String, String> backButton) {
        this.backButton = backButton;
    }

    public String getWrapperCssFile() {
        return wrapperCssFile;
    }

    public void setWrapperCssFile(String wrapperCssFile) {
        this.wrapperCssFile = wrapperCssFile;
    }

    public String getCustomCssFile() {
        return customCssFile;
    }

    public void setCustomCssFile(String customCssFile) {
        this.customCssFile = customCssFile;
    }

    public String getCustomCssString() {
        return customCssString;
    }

    public void setCustomCssString(String customCssString) {
        this.customCssString = customCssString;
    }
}
