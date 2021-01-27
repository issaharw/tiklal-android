




function scrollToCurrent() {
    var scrollTo = 0
    containerDiv.children().each( (index, child) => {
        var major = parseInt($(child).attr("major"))
        var minor = parseInt($(child).attr("minor"))
        if (major == 9 || (major == currentMajor && minor == currentMinor))
            return false
        
        scrollTo += $(contentId(major, minor)).height()
    });
    containerDiv.scrollTop(scrollTo + 1)
}

function updateCurrentFromScrollPosition() {
    var height = 0
    var major = 0
    var minor = 0
    containerDiv.children().each( (index, child) => {
        major = parseInt($(child).attr("major"))
        minor = parseInt($(child).attr("minor"))
        height += $(contentId(major, minor)).height()
        if (height >= containerDiv.scrollTop())
            return false
    });
    if (currentMajor != major || currentMinor != minor) {
        currentMajor = major
        currentMinor = minor
        updateTitleWithCurrent()
    }

}

function handleScrolling() {

    // scrolling up
    if (containerDiv.scrollTop() < 200) {
        var prev = getPrev(currentMajor, currentMinor)
        if (prev[0] != -1) {
            var major = prev[0]
            var minor = prev[1]
            if (!exists(major, minor)) {
                var section = createSection(major, minor)
                containerDiv.prepend(section)
            }
        }
    }
    
    // scrolling down
    var heightIncludingCurrent = 0
    containerDiv.children().each( (index, child) => {
        var major = parseInt($(child).attr("major"))
        var minor = parseInt($(child).attr("minor"))
        heightIncludingCurrent += $(contentId(major, minor)).height()
        if (major == currentMajor && minor == currentMinor)
            return false
    });
    if (containerDiv.scrollTop() + containerDiv.height() > heightIncludingCurrent - 200) {
        var next = getNext(currentMajor, currentMinor)
        if (next[0] == -1)
            return
        
        if (!exists(next[0], next[1])) {
            var section = createSection(next[0], next[1])
            containerDiv.append(section)
        }
    }
        
    updateCurrentFromScrollPosition()
    // console.log("Current Position: " + currentMajor + " : " + currentMinor)
    // console.log("Prev: " + getPrev(currentMajor, currentMinor) + ",  Next: " + getNext(currentMajor, currentMinor))
    // var contentDivId = currentContentId()
    // console.log("Container Height: " + containerDiv.height())
    // console.log("Current element Height: " + $(contentDivId).height())
    // console.log("Scroll Top: " + containerDiv.scrollTop())
}


function goToPrev() {
    if (containerDiv.is(":hidden"))
        return 
    var prev = getPrev(currentMajor, currentMinor)
    if (prev[0] == -1)
        return
    
    currentMajor = prev[0]
    currentMinor = prev[1]
    if (!exists(currentMajor, currentMinor)) {
        var section = createSection(currentMajor, currentMinor)
        containerDiv.prepend(section)
    }
    
    scrollToCurrent()
    updateTitleWithCurrent()
}

function goToNext() {
    if (containerDiv.is(":hidden"))
        return 
    var next = getNext(currentMajor, currentMinor)
    if (next[0] == -1)
        return
    
    currentMajor = next[0]
    currentMinor = next[1]
    if (!exists(currentMajor, currentMinor)) {
        var section = createSection(currentMajor, currentMinor)
        containerDiv.append(section)
    }
    scrollToCurrent()
    updateTitleWithCurrent()
}

function goUp() {
    if (containerDiv.is(":hidden"))
        buildMainMenu()
    else
        buildMinorMenu(currentMajor)
}