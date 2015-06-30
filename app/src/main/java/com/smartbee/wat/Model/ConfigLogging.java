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
 * http://wat-docs.azurewebsites.net/Json#logging
 */

public class ConfigLogging
{
    private boolean enabled;
    private String level;
    private boolean disableWithoutDebugger;
    private boolean hideTagDisplay;
    private String[] ignoreTags;
    private boolean logErrorsForIgnoredTags;
    private boolean disableConsoleLog;
    private ConfigLoggingFileLog fileLog;

    public ConfigLogging() {}

    // generated Getters and Setters

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public boolean isDisableWithoutDebugger() {
        return disableWithoutDebugger;
    }

    public void setDisableWithoutDebugger(boolean disableWithoutDebugger) {
        this.disableWithoutDebugger = disableWithoutDebugger;
    }

    public boolean isHideTagDisplay() {
        return hideTagDisplay;
    }

    public void setHideTagDisplay(boolean hideTagDisplay) {
        this.hideTagDisplay = hideTagDisplay;
    }

    public String[] getIgnoreTags() {
        return ignoreTags;
    }

    public void setIgnoreTags(String[] ignoreTags) {
        this.ignoreTags = ignoreTags;
    }

    public boolean isLogErrorsForIgnoredTags() {
        return logErrorsForIgnoredTags;
    }

    public void setLogErrorsForIgnoredTags(boolean logErrorsForIgnoredTags) {
        this.logErrorsForIgnoredTags = logErrorsForIgnoredTags;
    }

    public boolean isDisableConsoleLog() {
        return disableConsoleLog;
    }

    public void setDisableConsoleLog(boolean disableConsoleLog) {
        this.disableConsoleLog = disableConsoleLog;
    }

    public ConfigLoggingFileLog getFileLog() {
        return fileLog;
    }

    public void setFileLog(ConfigLoggingFileLog fileLog) {
        this.fileLog = fileLog;
    }
}
