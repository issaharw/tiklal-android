
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
}

// Function to apply/remove old colors dynamically
function applyOldColors(enable) {
    var existingLink = document.getElementById('oldColorsStylesheet');
    if (enable) {
        if (!existingLink) {
            var link = document.createElement('link');
            link.id = 'oldColorsStylesheet';
            link.rel = 'stylesheet';
            link.href = 'oldColors.css';
            document.head.appendChild(link);
        }
    } else {
        if (existingLink) {
            existingLink.remove();
        }
    }
}

// Apply old colors on load if enabled
if (oldColors) {
    document.addEventListener('DOMContentLoaded', function() {
        applyOldColors(true);
    });
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