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
 * http://wat-docs.azurewebsites.net/Json
 */

import java.util.regex.Pattern;

public class Config
{
    private String homeURL;
    private ConfigErrors errors;
    private ConfigNavigation navigation;
    private ConfigLogging logging;
    private ConfigShare share;
    private ConfigOffline offline;
    private ConfigCustomScript customScript; // not listed in JSON reference docs (NB: undefined items will cause NULLPointerException)
    private ConfigAppBar appBar; // translates to Action Bar
    private ConfigNavBar navBar;
    private ConfigLiveTile livetile; // livetile not used in Android
    private ConfigNotifications notifications;
    private ConfigRedirect redirects;
    private ConfigSettings settings;
    private ConfigStyles styles;
    private ConfigHeader header;
    private ConfigSearch search;
    private ConfigSecondaryTile secondaryPin; // secondaryPin not used in Android
    private String styleTheme;

    //Used to track if the config file has changed or been reloaded so we can force a reload elsewhere in the app
    private boolean hasConfigChanged;

    public Config()
    {
        // this.homeURL = "http://wat-docs.azurewebsites.net";
        hasConfigChanged = false;
    }

    // generated Getters and Setters

    public String getHomeURL() {
        return homeURL;
    }

    public void setHomeURL(String homeURL) {
        this.homeURL = homeURL;
    }

    public ConfigErrors getErrors() {
        return errors;
    }

    public void setErrors(ConfigErrors errors) {
        this.errors = errors;
    }

    public ConfigNavigation getNavigation() {
        return navigation;
    }

    public void setNavigation(ConfigNavigation navigation) {
        this.navigation = navigation;
    }

    public ConfigLogging getLogging() {
        return logging;
    }

    public void setLogging(ConfigLogging logging) {
        this.logging = logging;
    }

    public ConfigShare getShare() {
        return share;
    }

    public void setShare(ConfigShare share) {
        this.share = share;
    }

    public ConfigOffline getOffline() {
        return offline;
    }

    public void setOffline(ConfigOffline offline) {
        this.offline = offline;
    }

    public ConfigCustomScript getCustomScript() {
        return customScript;
    }

    public void setCustomScript(ConfigCustomScript customScript) {
        this.customScript = customScript;
    }

    public ConfigAppBar getAppBar() {
        return appBar;
    }

    public void setAppBar(ConfigAppBar appBar) {
        this.appBar = appBar;
    }

    public ConfigNavBar getNavBar() {
        return navBar;
    }

    public void setNavBar(ConfigNavBar navBar) {
        this.navBar = navBar;
    }

    public ConfigLiveTile getLivetile() {
        return livetile;
    }

    public void setLivetile(ConfigLiveTile livetile) {
        this.livetile = livetile;
    }

    public ConfigNotifications getNotifications() {
        return notifications;
    }

    public void setNotifications(ConfigNotifications notifications) {
        this.notifications = notifications;
    }

    public ConfigRedirect getRedirects() {
        return redirects;
    }

    public void setRedirects(ConfigRedirect redirects) {
        this.redirects = redirects;
    }

    public ConfigSettings getSettings() {
        return settings;
    }

    public void setSettings(ConfigSettings settings) {
        this.settings = settings;
    }

    public ConfigStyles getStyles() {
        return styles;
    }

    public void setStyles(ConfigStyles styles) {
        this.styles = styles;
    }

    public ConfigHeader getHeader() {
        return header;
    }

    public void setHeader(ConfigHeader header) {
        this.header = header;
    }

    public ConfigSearch getSearch() {
        return search;
    }

    public void setSearch(ConfigSearch search) {
        this.search = search;
    }

    public ConfigSecondaryTile getSecondaryPin() {
        return secondaryPin;
    }

    public void setSecondaryPin(ConfigSecondaryTile secondaryPin) {
        this.secondaryPin = secondaryPin;
    }

    public String getStyleTheme() {
        return styleTheme;
    }

    public void setStyleTheme(String styleTheme) {
        this.styleTheme = styleTheme;
    }

    public void handlePostLoad(boolean excludeLineStart, boolean excludeLineEnd) {
        this.hasConfigChanged = true;
        if (this.getRedirects() != null) {
            if (this.getRedirects().isEnabled()) {
                ConfigRedirectRules[] rules = this.getRedirects().getRules();
                for (int i = 0; i < rules.length; i++) {
                    ConfigRedirectRules rule = rules[i];
                    //Set Redirect Action Type
                    if (rule.getAction().equals("showMessage")) {
                        rule.setActionType(ConfigRedirectRules.RuleActionType.showMessage);
                    } else if (rule.getAction().equals("popout")) {
                        rule.setActionType(ConfigRedirectRules.RuleActionType.popout);
                    } else if (rule.getAction().equals("redirect")) {
                        rule.setActionType(ConfigRedirectRules.RuleActionType.redirect);
                    } else if (rule.getAction().equals("modal")) {
                        rule.setActionType(ConfigRedirectRules.RuleActionType.modal);
                    } else {
                        rule.setActionType(ConfigRedirectRules.RuleActionType.unknown);
                    }

                    //Build Regex Pattern for URL
                    rule.setRegexPattern(processPatternForRegex(rule.getPattern(), excludeLineStart, excludeLineEnd));
                    //Build Regex Pattern for match on close
                    if (rule.getCloseOnMatch() != null && !rule.getCloseOnMatch().isEmpty())
                        rule.setCloseOnMatchPattern((processPatternForRegex(rule.getCloseOnMatch(), excludeLineStart, excludeLineEnd)));
                }

                //TODO: Sort rules by order of action type
            }
        }
    }

    private Pattern processPatternForRegex(String pattern, boolean excludeLineStart, boolean excludeLineEnd) {
        String regexBody = pattern;
        regexBody = regexBody.replaceAll("\\{baseURL\\}", this.getHomeURL());
        //Log.d(WATActivity.TAG, "Before: " + regexBody);
        regexBody = regexBody.replaceAll("([.?*+^$\\[\\]\\\\(){}|-])", "\\\\$1");
        regexBody = regexBody.replace("\\?", ".?");
        regexBody = regexBody.replace("\\*", ".*?");
        //Log.d(WATActivity.TAG, "After: " + regexBody);
        if (!excludeLineStart) {
            regexBody = "^" + regexBody;
        }
        if (!excludeLineEnd) {
            regexBody += "$";
        }
        return Pattern.compile(regexBody);
    }

    public void handlePostLoad() {
        this.handlePostLoad(false, false);
    }

    public boolean getHasConfigChanged() { return this.hasConfigChanged; }
    public void markConfigUsed() { this.hasConfigChanged = false; }
}
