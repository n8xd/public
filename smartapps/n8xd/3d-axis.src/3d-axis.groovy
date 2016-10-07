/**
 *  The STAR
 *  Copyright 2015 
 */

definition(
    name: "3d Axis",
    namespace: "n8xd",
    author: "n8xd",
    description: "3d Axis",
    category: "Convenience",
    iconUrl: "http://cdn.mysitemyway.com/etc-mysitemyway/icons/legacy-previews/icons-256/glossy-waxed-wood-icons-symbols-shapes/018958-glossy-waxed-wood-icon-symbols-shapes-cube.png",
    iconX2Url: "http://cdn.mysitemyway.com/etc-mysitemyway/icons/legacy-previews/icons-256/glossy-waxed-wood-icons-symbols-shapes/018958-glossy-waxed-wood-icon-symbols-shapes-cube.png"
)

preferences {
    page(name: "mainPage", title: "", nextPage: "facePage", uninstall: true) {
        section("Use the orientation of this cube") {
            input "cube", "capability.threeAxis", required: false, title: "SmartSense Multi sensor"
        }
        section([title: " ", mobileOnly:true]) {
            label title: "Assign a name", required: false
            mode title: "Set for specific mode(s)", required: false
        }
    }
    page(name: "facePage", title: "Scenes", install: true, uninstall: true)
}

def facePage() {
    def faceId = getOrientation()
    def phrases = location.helloHome?.getPhrases()*.label
    phrases.sort()
    return dynamicPage(name:"facePage", nextPage:"", refreshInterval:5) {
        section {
            for (face in 1..6)
                input "${face}", "enum", title: "Face ${face} ${faceId==face ? ' (current)' : ''}", required: false, options: phrases
        }
        section {
            input "leave", "enum", title: "When leave home position", required: false, options: phrases
            input "home", "enum", title: "When back in home position", required: false, options: phrases
        }
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    subscribe cube, "threeAxis", positionHandler
    subscribe cube, "contact",   contactHandler
}

def positionHandler(evt) {

    def faceId = getOrientation(evt.xyzValue)
    
         log.trace "orientation: $faceId"
    
    if (faceId != state.lastActiveSceneId) {
        runHomeAction(faceId)
    }
    else {
        log.trace "No status change"
    }
    state.lastActiveSceneId = faceId
}

def contactHandler(evt) {

    def action = evt.value == "open" ? "leave" : "home"
    log.trace "contact ${evt.value} : $action"

    if (action != state.lastActiveAction) {
        runHomeAction(action)
    }
    else {
        log.trace "No status change"
    }
    state.lastActiveAction = action
}

private updateSetting(name, value) {
    app.updateSetting(name, value)
    settings[name] = value
}

private runHomeAction(faceId) {
    if (faceId in 1..6)
        location.helloHome.execute(settings."${faceId}")
    else {
        log.trace "No Home Action Defined for Current State"
    }
}

private getOrientation(xyz=null) {
    final threshold = 250

    def value = xyz ?: cube.currentValue("threeAxis")

    
    def orientation = 0
    if (isNear(value.y, 0) && isNear(value.z,1000)) { orientation = 1 }
    else if (isNear(value.y, 1000) && isNear(value.z,350)) { orientation = 2 }
    else if (isNear(value.y, 600) && isNear(value.z,-820)) { orientation = 3 }
    else if (isNear(value.y, -580) && isNear(value.z,-850)) { orientation = 4 }
    else if (isNear(value.y, -1000) && isNear(value.z,300)) { orientation = 5 }
    else if (isNear(value.x, 1000) && isNear(value.y,0) && isNear(value.z,0)) { orientation = 6 }
    
    // log.debug "${value.x}, ${value.y}, ${value.z} = orientation ${orientation}"   
    orientation
}

private isNear(w, d)
{
	def tol = 200
    
    return  Math.abs((w - d)) < tol 
       
 }