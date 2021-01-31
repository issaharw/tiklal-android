var currentMajor = null
var currentMinor = null

var elementCreators = {
    "P0": function (children, index) {
        var elt = window.document.createElement('div');
        elt.classList.add("P0");
        elt.setAttribute('data-pos', index);
        children.forEach(function (e) {
            elt.appendChild(e);
        });
        var eltAfter = window.document.createElement('div');
        eltAfter.classList.add("P0after");
        eltAfter.setAttribute('data-pos', index);
        var eltMain = window.document.createElement('div');
        eltMain.appendChild(elt);
        eltMain.appendChild(eltAfter);
        eltMain.setAttribute('data-pos', index);
        return eltMain;
    },
    "P1": function (children, index) {
        var elt = window.document.createElement('div');
        elt.classList.add("P1");
        elt.setAttribute('data-pos', index);
        children.forEach(function (e) {
            elt.appendChild(e);
        });
        var eltAfter = window.document.createElement('div');
        eltAfter.classList.add("P1after");
        eltAfter.setAttribute('data-pos', index);
        var eltMain = window.document.createElement('div');
        eltMain.appendChild(elt);
        eltMain.appendChild(eltAfter);
        eltMain.setAttribute('data-pos', index);
        return eltMain;
    },
    "P2": function (children, index) {
        var elt = window.document.createElement('div');
        elt.classList.add("P2");
        elt.setAttribute('data-pos', index);
        children.forEach(function (e) {
            elt.appendChild(e);
        });
        var eltAfter = window.document.createElement('div');
        eltAfter.classList.add("P2after");
        eltAfter.setAttribute('data-pos', index);
        var eltMain = window.document.createElement('div');
        eltMain.appendChild(elt);
        eltMain.appendChild(eltAfter);
        eltMain.setAttribute('data-pos', index);
        return eltMain;
    },
    "P5": function (children, index) {
        var elt = window.document.createElement('div');
        elt.classList.add("P5");
        elt.setAttribute('data-pos', index);
        children.forEach(function (e) {
            elt.appendChild(e);
        });
        var eltAfter = window.document.createElement('div');
        eltAfter.classList.add("P5after");
        eltAfter.setAttribute('data-pos', index);
        var eltMain = window.document.createElement('div');
        eltMain.appendChild(elt);
        eltMain.appendChild(eltAfter);
        eltMain.setAttribute('data-pos', index);
        return eltMain;
    },
    "P6": function (children, index) {
        var elt = window.document.createElement('div');
        elt.classList.add("P6");
        elt.setAttribute('data-pos', index);
        children.forEach(function (e) {
            elt.appendChild(e);
        });
        var eltAfter = window.document.createElement('div');
        eltAfter.classList.add("P6after");
        eltAfter.setAttribute('data-pos', index);
        var eltMain = window.document.createElement('div');
        eltMain.appendChild(elt);
        eltMain.appendChild(eltAfter);
        eltMain.setAttribute('data-pos', index);
        return eltMain;
    },
    "C4": function (children, index) {
        var elt = window.document.createElement('div');
        elt.classList.add("C4");
        elt.setAttribute('data-pos', index);
        children.forEach(function (e) {
            elt.appendChild(e);
        });
        return elt;
    },
    "C5": function (children, index) {
        var elt = window.document.createElement('div');
        elt.classList.add("C5");
        elt.setAttribute('data-pos', index);
        children.forEach(function (e) {
            elt.appendChild(e);
        });
        return elt;
    },
    "C6": function (children, index) {
        var elt = window.document.createElement('div');
        elt.classList.add("C6");
        elt.setAttribute('data-pos', index);
        children.forEach(function (e) {
            elt.appendChild(e);
        });
        return elt;
    },
    "C11": function (children, index) {
        var elt = window.document.createElement('div');
        elt.classList.add("C11");
        elt.setAttribute('data-pos', index);
        children.forEach(function (e) {
            elt.appendChild(e);
        });
        return elt;
    },
    "C21": function (children, index) {
        var elt = window.document.createElement('div');
        elt.classList.add("C21");
        elt.setAttribute('data-pos', index);
        children.forEach(function (e) {
            elt.appendChild(e);
        });
        return elt;
    },
    "C31": function (children, index) {
        var elt = window.document.createElement('div');
        elt.classList.add("C31");
        elt.setAttribute('data-pos', index);
        children.forEach(function (e) {
            elt.appendChild(e);
        });
        return elt;
    },
    "br": function (children, index) {
        var elt = window.document.createElement('br');
        elt.setAttribute('data-pos', index);
        return elt;
    },
    "ul": function (children, index) {
        var elt = window.document.createElement('ul');
        elt.setAttribute('data-pos', index);
        children.forEach(function (e) {
            elt.appendChild(e);
        });
        return elt;
    },
    "li": function (children, index) {
        var elt = window.document.createElement('li');
        elt.setAttribute('data-pos', index);
        children.forEach(function (e) {
            elt.appendChild(e);
        });
        return elt;
    },
    "A": function (children, index) {
        var elt = window.document.createElement('a');
        elt.classList.add("a-text");
        elt.setAttribute('data-pos', index);
        elt.setAttribute('href', children[0].nodeValue);
        elt.setAttribute('target', '_blank');
        elt.appendChild(children[1]);
        return elt;
    },
    "END": function (children, index) {
        var elt = window.document.createElement('img');
        elt.setAttribute('data-pos', index);
        elt.setAttribute('src', 'end.svg');
        elt.style.width = '33.33vw';
        elt.style.height = '20.79vw';
        elt.style.margin = '5vw 33.33vw 5vw';
        elt.style.display = 'block';
        var eltMain = window.document.createElement('div');
        eltMain.appendChild(elt);
        eltMain.setAttribute('data-pos', index);
        //children.forEach(function(e){elt.appendChild(e);});
        return eltMain;
    }
};

function getElement(je, index) {
    if (je === null) {
        je = "";
    }
    if (typeof je == "string") {
        return window.document.createTextNode(je);
    }
    var type = Object.keys(je)[0];
    var children;
    if ((typeof je[type] == "object") && Array.isArray(je[type])) {
        children = je[type].map(getElement);
    } else {
        children = [getElement(je[type])];
    }
    return elementCreators[type](children, index);
}

function getNumberOfMinors(major) {
    var majorObject = sidur.content[major]
    var majorTitle = Object.keys(majorObject)[0]
    return majorObject[majorTitle].length
}

function getPrev(major, minor) {
    if (minor > 0)
        return [major, minor -1]
    else if (major == 0)
        return [-1,-1]
    else {
        var numberOfMinors = getNumberOfMinors(major - 1)
        return [major - 1, numberOfMinors -1]
    }
}

function getNext(major, minor) {
    var numberOfMinors = getNumberOfMinors(major)
    if (minor < numberOfMinors - 1) 
        return [major, minor + 1]
    else if (major == 8)
        return [-1, -1]
    else
        return [major + 1, 0]
}

function getContent(major, minor) {
    if (major > 8)
        return {
            "error": "Index of major is out of bounds. There are only 9 majors"
        }
    var majorObject = sidur.content[major]
    var majorTitle = Object.keys(majorObject)[0]
    var minorObject = majorObject[majorTitle][minor]
    if (!minorObject)
        return {
            "error": "Index of minor is out of bounds. There are only " + majorObject[majorTitle].length + " elements in '" + majorTitle + "'"
        }
    var minorTitle = Object.keys(minorObject)[0]
    var object = minorObject[minorTitle]

    return {
        "major": majorTitle,
        "minor": minorTitle,
        "data": object,
        "numberOfMinors": majorObject[majorTitle].length
    }
}

function createSection(major, minor) {
    var div = $("<div id='content-" + major + "-" + minor + "' major='" + major + "' minor='" + minor + "'>")
    var content = getContent(major, minor).data
    for (var i = 0; i < content.length; i++) {
        var elem = getElement(content[i], i)
        div.append(elem)
    }
    return div
}

function getMajorTitles() {
    var ret = []
    for (major of sidur.content) {
        ret.push(Object.keys(major)[0])
    }
    return ret
}

function getMinorTitles(major) {
    var ret = []
    var majorObject = sidur.content[major]
    var majorTitle = Object.keys(majorObject)[0]
    var minors = majorObject[majorTitle]
    for (minor of minors) {
        ret.push(Object.keys(minor)[0])
    }
    return ret
}


function updateTitle(majorTitle, minorTitle) {
    titleDiv.empty()
    if (minorTitle != null) {
        titleDiv.append("<span class='titleWithMajorMinor'>" + majorTitle + "<br>" + minorTitle + "</span>")
    }
    else {
        titleDiv.append("<span class='titleWithMajorOnly'>" + majorTitle + "</span>")
    }
}

function updateTitleWithCurrent() {
    var majorTitle = getMajorTitles()[currentMajor]
    var minorTitle = getMinorTitles(currentMajor)[currentMinor]    
    updateTitle(majorTitle, minorTitle)
}


function contentId(major, minor) {
    return "#content-" + major + "-" + minor
}

function currentContentId() {
    return contentId(currentMajor, currentMinor)
}


function exists(major, minor) {
    var exists = false
    containerDiv.children().each( (index, child) => {
        var mj = parseInt($(child).attr("major"))
        var mn = parseInt($(child).attr("minor"))
        if (mj == major && mn == minor) {
            exists = true            
            return false
        }
    });
    return exists
}

