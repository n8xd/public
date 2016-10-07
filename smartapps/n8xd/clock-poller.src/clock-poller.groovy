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
 *  Author: SmartThings
 *  2/16/2016  adapted to join a door sensor to a polled device N8XD
 */
definition(
    name: "Clock Poller",
    namespace: "n8xd",
    author: "n8xd",
    description: "Watch a door switch attached to a clock and poll something at regular intervals.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/window_contact.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/window_contact@2x.png"
)

preferences {
	section("Which Clock has the door sensor?"){
		input "contact1", "capability.contactSensor", title: "Chose your clock:"
	}
        section("What do you want to poll?") {
                input "poller1", "capability.polling", title: "Choose your device:"
        }
}

def installed()
{
	subscribe(contact1, "contact.closed", contactClosedHandler)
}

def updated()
{
	unsubscribe()
	subscribe(contact1, "contact.closed", contactClosedHandler)
}

def contactClosedHandler(evt) {
	log.trace "$evt.value: $evt, $settings"
	log.debug "${poller1.label ?: poller1.name} polling!"
    poller1.poll()

}