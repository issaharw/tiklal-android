
var menuDiv = $("#menu")
var containerDiv = $("#container")
var settingsDiv = $("#settings")
var titleDiv = $("#titleDiv")
var searchDiv = $("#search")
var searchResultsDiv = $("#searchResults")


var isIpad = false
var darkMode = false
var oldColors = false
var fontSize = 2

if (window.location.search.indexOf('ipad=true') >= 0) {
    isIpad = true
    document.write('<link rel="stylesheet" href="ipad.css" />');
}
    
if (window.location.search.indexOf('oldColors=true') >= 0) {
    oldColors = true
    document.write('<link rel="stylesheet" href="oldColors.css" />');
}
    
if (window.location.search.indexOf('darkMode=true') >= 0) {
    darkMode = true
}
    
if (window.location.search.indexOf('fontSize=') >= 0) {
    var idx = window.location.search.indexOf('fontSize=') + 9
    fontSize = parseInt(window.location.search.charAt(idx))
}


applySettings()
buildMainMenu()
containerDiv.scroll(function() { handleScrolling() });