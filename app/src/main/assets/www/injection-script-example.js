// This script will be executed in the context of the webview
var foo = function () {
    document.getElementsByTagName("body")[0].style.color = "red";
};

foo();