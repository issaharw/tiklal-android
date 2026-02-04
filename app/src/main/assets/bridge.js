// Bridge for communication between WebView and native Android

var lastReportedMajor = -1;
var lastReportedMinor = -1;

// Called when position changes (from scrolling or navigation)
function reportPositionToNative() {
    if (typeof AndroidBridge === 'undefined') {
        return;
    }
    
    // Only report if position actually changed
    if (currentMajor === lastReportedMajor && currentMinor === lastReportedMinor) {
        return;
    }
    
    lastReportedMajor = currentMajor;
    lastReportedMinor = currentMinor;
    
    var majorTitle = getMajorTitles()[currentMajor] || "";
    var minorTitle = getMinorTitles(currentMajor)[currentMinor] || "";
    
    AndroidBridge.onPositionChanged(currentMajor, currentMinor, majorTitle, minorTitle);
}

// Called by native to get current position
function getCurrentPosition() {
    var majorTitle = getMajorTitles()[currentMajor] || "";
    var minorTitle = getMinorTitles(currentMajor)[currentMinor] || "";
    
    return JSON.stringify({
        major: currentMajor,
        minor: currentMinor,
        majorTitle: majorTitle,
        minorTitle: minorTitle
    });
}

// Called by native to navigate to a specific position
function navigateToPosition(major, minor) {
    showContent(major, minor);
}

// Hook into the existing updateTitleWithCurrent function to report position changes
var originalUpdateTitleWithCurrent = updateTitleWithCurrent;
updateTitleWithCurrent = function() {
    originalUpdateTitleWithCurrent();
    reportPositionToNative();
};

// Also hook into showContent to catch direct navigation
var originalShowContent = showContent;
showContent = function(major, minor, dataPositionsToHighlight) {
    originalShowContent(major, minor, dataPositionsToHighlight || []);
    // Position will be reported via updateTitleWithCurrent which is called inside showContent
};

// Check on load if we should restore a saved position
function checkAndRestorePosition() {
    if (typeof AndroidBridge !== 'undefined') {
        AndroidBridge.onWebViewReady();
    }
}

// Initialize when page is ready
$(document).ready(function() {
    // Small delay to ensure everything is loaded
    setTimeout(checkAndRestorePosition, 100);
});
