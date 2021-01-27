function applySettings() {
    if (darkMode) 
        containerDiv.addClass("containerDark")
    else    
        containerDiv.removeClass("containerDark")

    if (fontSize == 1)
        $("html").css("font-size", "0.8vw")
    if (fontSize == 2)
        $("html").css("font-size", "1vw")
    if (fontSize == 3)
        $("html").css("font-size", "1.2vw")
    if (fontSize == 4)
        $("html").css("font-size", "1.4vw")
}


function toggleDarkMode(element) {
    darkMode = !darkMode
    if (darkMode) {
        $(element).text('מופעל')
    }
    else {
        $(element).text('כבוי')    
    }

    applySettings()
    window.open("settings://tiklal?darkMode=" + darkMode , '_blank');
}

function toggleOldColors(element) {
    oldColors = !oldColors
    if (oldColors) {
        $(element).text('מופעל')
    }
    else {
        $(element).text('כבוי')    
    }

    applySettings()
    window.open("settings://tiklal?oldColors=" + oldColors , '_blank');
}

function setFontSize(element, value) {
    if (value == fontSize)
        return

    fontSize = value

    for(var i=1; i <=4; i++) {
        var fontSizeElementId = "#fontSize" + i
        if (i == fontSize)
            $(fontSizeElementId).css("text-decoration", "none")
        else
            $(fontSizeElementId).css("text-decoration", "underline")
    }

    applySettings()
    window.open("settings://tiklal?fontSize=" + fontSize , '_blank');
}

function showSettingsScreen() {
    if (darkMode) {
        $("#darkModeToggle").text('מופעל')
    }
    else {
        $("#darkModeToggle").text('כבוי')    
    }
    if (oldColors) {
        $("#oldColorsToggle").text('מופעל')
    }
    else {
        $("#oldColorsToggle").text('כבוי')    
    }
    var fontSizeElementId = "#fontSize" + fontSize
    $(fontSizeElementId).css("text-decoration", "none")

    updateTitle("הגדרות")
    containerDiv.hide()
    menuDiv.hide()
    settingsDiv.show()
} 
