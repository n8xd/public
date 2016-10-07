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
 *  6/2/2016  Contact Sensor doorbell detection, plays MP3 through a musicPlayer capable device
 */
definition(
    name: "Doorbell Speaker Notify",
    namespace: "n8xd",
    author: "Keith DeLong",
    description: "Play a doorbell MP3 through the speakers, when the doorbell contact is open.",
    category: "Convenience",
    iconUrl: "http://cdn.device-icons.smartthings.com/Electronics/electronics16-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Electronics/electronics16-icn@2x.png"
)

preferences {
	section("Which Doorbell Contact?"){
		input "contact1", "capability.contactSensor", title: "Doorbell Contact Closure", required:true, multiple:false
	}
        section("Music Player Device:") {
                input "vlctalk","capability.musicPlayer",title: "Select Music Player Device / VLC Thing / ..." ,required:true, multiple:false
        }
        section("MP3 to play:") {
                input "webaddr", "string", title: "Full Web Address", required:true, multiple: false
                input "lenny", "number", title: "How long do you want it to play (max seconds)?", required:true, multiple: false, default: 8
        }
}

def installed()
{
	subscribe(contact1, "contact.open", dingDong)
}

def updated()
{
	unsubscribe()
	subscribe(contact1, "contact.open", dingDong)
}

def dingDong(evt) {
	log.trace "$evt.value: $evt, $settings"
	log.debug "Playing ${webaddr} through ${vlctalk}"
    
    vlctalk.playTrackAndResume(webaddr, lenny)

}