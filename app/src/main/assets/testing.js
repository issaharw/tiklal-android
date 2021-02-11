
var major = 0
var minor = 0
while (true) {
    if (major == NUMBER_OF_MAJORS)
        break
    var content = getContent(major, minor)
    console.log(major + ":" + minor + "  " + content.major + " - " + content.minor)
    var next = getNext(major, minor)
    major = next[0]
    minor = next[1]
}
