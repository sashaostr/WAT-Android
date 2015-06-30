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
 * http://wat-docs.azurewebsites.net/Json#livetile
 */

public class ConfigLiveTile
{
    private boolean enabled;
    private int periodicUpdate;
    private boolean enableQueue;
    private String tilePollFeed;

    public ConfigLiveTile() {}

    // generated Getters and Setters

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getPeriodicUpdate() {
        return periodicUpdate;
    }

    public void setPeriodicUpdate(int periodicUpdate) {
        this.periodicUpdate = periodicUpdate;
    }

    public boolean isEnableQueue() {
        return enableQueue;
    }

    public void setEnableQueue(boolean enableQueue) {
        this.enableQueue = enableQueue;
    }

    public String getTilePollFeed() {
        return tilePollFeed;
    }

    public void setTilePollFeed(String tilePollFeed) {
        this.tilePollFeed = tilePollFeed;
    }
}