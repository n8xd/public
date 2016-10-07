/**
 *  Copyright 2015 Keith DeLong
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
 *  Author: Keith DeLong
 *  6/2/2016  Contact Sensors 
 */
definition(
    name: "Pool Level",
    namespace: "n8xd",
    author: "n8xd",
    description: "Use Four contact Sensors to monitor the pool level.",
    category: "Convenience",
    iconUrl: "http://cdn.device-icons.smartthings.com/Electronics/electronics16-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Electronics/electronics16-icn@2x.png"
)

preferences {
	section("Contacts"){
		input "contact0", "capability.contactSensor", title: "Contact 0 (Lowest)", required:true, multiple:false
		//input "contact1", "capability.contactSensor", title: "Contact 1 (Low Middle)", required:true, multiple:false
		//input "contact2", "capability.contactSensor", title: "Contact 2 (High Middle)", required:true, multiple:false
		input "contact3", "capability.contactSensor", title: "Contact 3 (Highest)", required:true, multiple:false
	}
}

def installed()
{
	subscribe(contact0, "contact.open", marker)
	//subscribe(contact1, "contact.open", marker)
	//subscribe(contact2, "contact.open", marker)
	subscribe(contact3, "contact.open", marker)
}

def updated()
{
	unsubscribe()
	subscribe(contact0, "contact.open", marker)
	//subscribe(contact1, "contact.open", marker)
	//subscribe(contact2, "contact.open", marker)
	subscribe(contact3, "contact.open", marker)
}

def marker(evt) {
	log.trace "$evt.value: $evt, $settings"
        // call poll on each device, so it can update state variable for the 4 contacts.  (each driver can show level that way)
}