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
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Turn It On When I'm Here
 *
 *  Author: SmartThings
 */
definition(
    name: "Turn It On When I'm Here and not holiday",
    namespace: "smartthings",
    author: "SmartThings",
    description: "Turn something on at a time when I am home and its not a holiday.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_presence-outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_presence-outlet@2x.png"
)

preferences {
	section("When I arrive and leave..."){
		input "presence1", "capability.presenceSensor", title: "Who?", multiple: true
        input "holidaySwitch","capability.switch",title: "Holiday Switch", multiple:false
	}
	section("Turn on/off a light..."){
		input "lights", "capability.colorControl", multiple: true
	}
 	section("Turn them all on at...") {
		input name: "startTime", title: "Turn On Time?", type: "time"
	}
	section("And turn them off at...") {
		input name: "stopTime", title: "Turn Off Time?", type: "time"
	}
    section("On Which Days") {
        input "days", "enum", title: "Select Days of the Week", required: true, multiple: true, options: ["Sunday":"Sunday","Monday": "Monday", "Tuesday": "Tuesday", "Wednesday": "Wednesday", "Thursday": "Thursday", "Friday": "Friday","Saturday":"Saturday"]
    }   
}


def installed() {
	log.debug "Installed with settings: ${settings}"
	schedule(startTime, "startTimerCallback")
	schedule(stopTime, "stopTimerCallback")
}

def updated(settings) {
	unschedule()
	schedule(startTime, "startTimerCallback")
	schedule(stopTime, "stopTimerCallback")
}



def startTimerCallback() {
	
    log.debug "Turning on switches"
    def df = new java.text.SimpleDateFormat("EEEE")
    // Ensure the new date object is set to local time zone
    df.setTimeZone(location.timeZone)
    def day = df.format(new Date())
	def dayCheck=days.contains(day)    
    if (dayCheck){
        if (holidaySwitch==1) {
            log.debug "presenceHandler $evt.name: $evt.value"
            def current = presence1.currentValue("presence")
            log.debug current
            def presenceValue = presence1.find{it.currentPresence == "present"}
            log.debug presenceValue
            if(presenceValue){
                lights.on()
                log.debug "Someone's home and not holiday-turn on lights!"
            }
        }
        else{
            log.debug "Everyone's away."

        }
	}
}

def stopTimerCallback() {
	log.debug "Turning off switches"
    def df = new java.text.SimpleDateFormat("EEEE")
    // Ensure the new date object is set to local time zone
    df.setTimeZone(location.timeZone)
    def day = df.format(new Date())
	def dayCheck=days.contains(day)    
    if (dayCheck){
        if (holidaySwitch==1){
	        lights.off()
            log.debug "Stop timer "
        }
    }
}