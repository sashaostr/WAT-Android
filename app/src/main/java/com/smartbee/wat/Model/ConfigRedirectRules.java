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
 * http://wat-docs.azurewebsites.net/Json#redirects
 */

import java.util.regex.Pattern;

public class ConfigRedirectRules
{
    private String pattern;
    private Pattern regexPattern;
    private String action;
    private String message;
    private String url;
    private RuleActionType actionType;
    private boolean hideCloseButton;
    private String closeOnMatch;
    private Pattern closeOnMatchPattern;

    public enum RuleActionType {
        showMessage,
        popout,
        redirect,
        modal,
        unknown
    }

    public ConfigRedirectRules() {}

    // generated Getters and Setters

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Pattern getRegexPattern() { return this.regexPattern; }
    public void setRegexPattern(Pattern regexPattern) { this.regexPattern = regexPattern; }

    public RuleActionType getActionType() { return this.actionType; }
    public void setActionType(RuleActionType actionType) { this.actionType = actionType; }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() { return this.message; }
    public void setMessage(String message) { this.message = message; }

    public String getUrl() { return this.url; }
    public void setUrl(String url) { this.url = url; }

    public boolean isHideCloseButton() {
        return hideCloseButton;
    }

    public void setHideCloseButton(boolean hideCloseButton) {
        this.hideCloseButton = hideCloseButton;
    }

    public String getCloseOnMatch() {
        return closeOnMatch;
    }
    public void setCloseOnMatch(String closeOnMatch) {
        this.closeOnMatch = closeOnMatch;
    }

    public Pattern getCloseOnMatchPattern() { return closeOnMatchPattern; }
    public void setCloseOnMatchPattern(Pattern closeOnMatchPattern) { this.closeOnMatchPattern = closeOnMatchPattern; }
}
