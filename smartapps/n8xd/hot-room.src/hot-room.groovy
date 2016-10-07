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
    name: "Hot Room",
    namespace: "n8xd",
    author: "n8xd",
    description: "SMS Notify on too Hot.",
    category: "Convenience",
    iconUrl: "http://cdn.device-icons.smartthings.com/Appliances/appliances4-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Appliances/appliances4-icn@2x.png"
)

preferences {
    section("This is not a safety app, it will not save your life.") {}

    section("Which Temperature Detector is in the Room?"){
		input "roomTemp1", "capability.temperatureMeasurement", title: "Temperature Sensor:"
    }
    section("What is the name of the room?"){
        input "roomName", "string",title: "Room name:"
    }
    section("When the app starts, it may not receive a temperature report from one or both sensors for a while") {
        input "defaultTemp", "number", title: "Default Temp: ", defaultValue: 0
    }
    section("Warn when room temperature is hotter than this maximum") {
    	input "roomMaxTemp", "number", title: "Room Warning Temp: ", defaultValue: 80
    }
    section("Phone numbers to contact") {
        input "phone1", "phone", title: "Phone Number for SMS, optional", required: false
        input "phone2", "phone", title: "Phone Number for SMS, optional", required: false
        input "phone3", "phone", title: "Phone Number for SMS, optional", required: false
        input "phone4", "phone", title: "Phone Number for SMS, optional", required: false
    }
    section("(Optional) Flip a switch when Warning Temp or Differential Temp is exceeded.  And turn it off all other times.") {
        input "switch1", "capability.switch", title: "Switch:", required: false
     }
    
}

def installed()
{
	subscribe(roomTemp1, "temperature", roomTempHandler)
}

def updated()
{
	unsubscribe()
        subscribe(roomTemp1, "temperature", roomTempHandler)
}


def roomTempHandler(evt) {
    state.roomTemp = evt.value
    log.trace "Got room temp: $state.roomTemp"
    roomTemp(evt)
}
    
    
def roomTemp(evt) {

    Map options = [:]
    String msg 
    
    if (state.roomTemp == null) {state.roomTemp = defaultTemp}

    sendEvent(name: "room", value: "note", descriptionText: " Room Temp: $state.roomTemp")
    
    def a = state.roomTemp.toInteger()
    msg = "WARNING: The ${roomName} temperature is ${a} degrees"
    
    if (a >= roomMaxTemp) {
        log.warn "${msg}"
        sendEvent(name: "room", value: "WARNING", descriptionText: "${msg}")
        if (phone1) {options.phone = phone1; sendNotification(msg, options) }
        if (phone2) {options.phone = phone2; sendNotification(msg, options) }
        if (phone3) {options.phone = phone3; sendNotification(msg, options) }
        if (phone4) {options.phone = phone4; sendNotification(msg, options) }
        if (switch1) { switch1.on() }
    }
    else
    {
       if (switch1) { switch1.off() }
    }
	
}