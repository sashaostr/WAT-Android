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
 * http://wat-docs.azurewebsites.net/Json#errors
 */

public class ConfigErrors
{
    private boolean showAlertOnError;
    private String alertMessage;
    private boolean redirectToErrorPage;
    private String errorPageURL;

    public ConfigErrors() {}

    // generated Getters and Setters

    public boolean isShowAlertOnError() {
        return showAlertOnError;
    }

    public void setShowAlertOnError(boolean showAlertOnError) {
        this.showAlertOnError = showAlertOnError;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public boolean isRedirectToErrorPage() {
        return redirectToErrorPage;
    }

    public void setRedirectToErrorPage(boolean redirectToErrorPage) {
        this.redirectToErrorPage = redirectToErrorPage;
    }

    public String getErrorPageURL() {
        return errorPageURL;
    }

    public void setErrorPageURL(String errorPageURL) {
        this.errorPageURL = errorPageURL;
    }
}
