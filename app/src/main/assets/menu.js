function buildMainMenu() {
    menuDiv.empty()
    var list = $("<ul class='menuList'>")
    menuDiv.append(list)
    var titles = getMajorTitles()
    for(i = 0; i < titles.length; i++) {
        var item = $("<li>")
        list.append(item)
        var majorElem = $("<span class='menuItem'>").text(titles[i])
                                .attr("onclick", "buildMinorMenu(" + i + ")")
        item.append(majorElem)
    }
    currentMajor = 0
    currentMinor = 0
    updateTitle('סידור תפילה בנוסח הקדום והזך של', 'קהילות קודש תימן יע"א – בלדי')
    settingsDiv.hide()
    containerDiv.hide()
    menuDiv.show()
                    
} 

function buildMinorMenu(major) {
    updateTitle(getMajorTitles()[major])
    menuDiv.empty()
    var list = $("<ul class='menuList'>")
    menuDiv.append(list)
    var titles = getMinorTitles(major)
    for(i = 0; i < titles.length; i++) {
        var item = $("<li>")
        list.append(item)
        var minorElem = $("<span class='menuItem'>").text(titles[i])
                                .attr("onclick", "showContent(" + major + ", " + i + ")")
        item.append(minorElem)
    }

    settingsDiv.hide()
    containerDiv.hide()
    menuDiv.show()
}


function showContent(major, minor) {
    currentMajor = major
    currentMinor = minor
    containerDiv.empty()
    var prev = getPrev(major, minor) 
    if (prev[0] != -1) {
        var section = createSection(prev[0], prev[1])
        containerDiv.append(section)
    }
    var section = createSection(major, minor)
    containerDiv.append(section)
    var next = getNext(major, minor) 
    if (next[0] != -1) {
        var section = createSection(next[0], next[1])
        containerDiv.append(section)
    }
    
    settingsDiv.hide()    
    menuDiv.hide()
    containerDiv.show()
    scrollToCurrent()
    updateTitleWithCurrent()
}

