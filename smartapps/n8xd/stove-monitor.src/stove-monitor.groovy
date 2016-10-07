/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License
 *
 *  Author: n8xd
 *  
 *           Compare two temperature sensors as they change over time.
 *           This is not a saftey program...it will not save your life.
 *
 *   TODO: Add in deltaTime (time above temperature)
 *   TODO: Add more anoying alerts like SMS, PUSH, etc.
 *   4/19/16 KHD (n8xd) added optional switch turn on when temperature difference or max exceeded
 *   4/19/16 KHD (n8xd) added switch turn off in all other cases
 */
 
definition(
    name: "Stove Monitor",
    namespace: "n8xd",
    author: "n8xd",
    description: "Check temp over stove compared to room temp.",
    category: "Convenience",
    iconUrl: "http://cdn.device-icons.smartthings.com/Appliances/appliances4-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Appliances/appliances4-icn@2x.png"
)

preferences {
    section("This is not a safety app, it will not save your life.") {}

    section("Which Detector has the Near Stove Temperature Sensor?"){
		input "tempstove1", "capability.temperatureMeasurement", title: "Near Stove Sensor:"
	}
    section("Which Detector has the Room Temperature Sensor") {
                input "temproom1", "capability.temperatureMeasurement", title: "Room Sensor:"
    }
    section("When the app starts, it may not receive a temperature report from one or both sensors for a while") {
        input "defaultTemp", "number", title: "Default Temp: ", defaultValue: 0
    }
    section("Warn when stove temperature is hotter than this maximum") {
    	input "stoveMaxTemp", "number", title: "Stove Warning Temp: ", defaultValue: 120
    }
    section("Warn when stove temperature zone is this much greater than the room temperature" /*for a period of time*/) {
    	input "deltaTemp", "number", title: "Differential Temperature: ", defaultValue: 20
        // TODO input "deltaTime", "number", title: "For how long (minutes): ", defaultValue: 10
    }
    section("(Optional) Flip a switch when Warning Temp or Differential Temp is exceeded.  And turn it off all other times.") {
        input "switch1", "capability.switch", title: "Switch:", required: false
     }
    
}

def installed()
{
	    subscribe(tempstove1, "temperature", tempStoveHandler)
        subscribe(temproom1, "temperature", tempRoomHandler)
}

def updated()
{
	unsubscribe()
        subscribe(tempstove1, "temperature", tempStoveHandler)
        subscribe(temproom1, "temperature", tempRoomHandler)
}

def tempStoveHandler(evt) {
	state.stoveTemp = evt.value
    log.trace "Got stove temp: $state.stoveTemp"
    tempHandler(evt)
}

def tempRoomHandler(evt) {
	state.roomTemp = evt.value
    log.trace "Got room temp: $state.roomTemp"
    tempHandler(evt)
}
    
    
def tempHandler(evt) {

    if (state.roomTemp == null) {state.roomTemp = defaultTemp}
    if (state.stoveTemp == null) {state.stoveTemp = defaultTemp}
    

    sendEvent(name: "stove", value: "note", descriptionText: "Stove Temp: $state.stoveTemp, Room Temp: $state.roomTemp")
    def a = state.stoveTemp.toInteger()
    def b = state.roomTemp.toInteger()
    def c = a - b
    
    log.trace "Stove Temp: $state.stoveTemp, Room Temp: $state.roomTemp"
    sendEvent(name: "stove", value: "note", descriptionText: "Stove Temp: $a, Room Temp: $b")
    
    if (a >= stoveMaxTemp) {
        log.warn "WARNING: Near the Stove temperature is hotter than $a degrees"
        sendEvent(name: "stove", value: "WARNING", descriptionText: "WARNING: Near the Stove temperature is hotter than $a degrees")
        if (switch1) { switch1.on() }
    }
    
    if (a < b) {
        log.trace "Above the Stove is $c.abs() degrees Cooler than the Room temperature"
        sendEvent(name: "stove", value: "note", descriptionText: "Above the Stove is $c.abs() degrees Cooler than the Room temperature")
    	if (switch1) { switch1.off() }
    } else if (a == b) {
        log.trace "Near the Stove is the SAME temperature as the Room temperature"
        sendEvent(name: "stove", value: "note", descriptionText: "Near the Stove is the SAME temperature as the Room temperature")
 		if (switch1) { switch1.off() }   
    } else if ((a > b) && (c >= deltaTemp)) {
        log.warn "WARNING: Near the Stove is $c degress Hotter than the Room Temperature"
        sendEvent(name: "stove", value: "WARNING", descriptionText: "WARNING: Near the Stove is $c degress Hotter than the Room Temperature")
        if (switch1) { switch1.on() }
    } else if (a > b) {
    	log.trace "Near the Stove is $c degrees Hotter than the Room temperature"
        sendEvent(name: "stove", value: "note", descriptionText: "Near the Stove is $c degrees Hotter than the Room temperature")
		if (switch1) { switch1.off() }
    } else {
    	log.trace "What condition got us here?"
    }
	
}

