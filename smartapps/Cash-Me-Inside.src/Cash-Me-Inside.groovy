definition(
    name: "Cash Me Inside",
    namespace: "FryAllenEthan",
    author: "FryAllenEthan, TechWithJake",
    description: "Turn on a virtual switches that corelate to your room location ",
    iconUrl: "https://raw.githubusercontent.com/FryAllenEthan/Cash-Me-Inside/master/smartapps/Cash-Me-Inside.src/compass.png",
    iconX2Url: "https://raw.githubusercontent.com/FryAllenEthan/Cash-Me-Inside/master/smartapps/Cash-Me-Inside.src/compass.png",
    iconX3Url: "https://raw.githubusercontent.com/FryAllenEthan/Cash-Me-Inside/master/smartapps/Cash-Me-Inside.src/compass.png",
    )

preferences {
 page(name: "appSetup")
 page(name: "credentialsSetup")
 page(name: "switchSetup")
}

def appSetup(){
  dynamicPage(name: "appSetup", title: "FIND Setup", install: true, uninstall: true) {
    section("FIND Credentials Setup"){
      href(name: "toCredentialsSetup", page: "credentialsSetup", title: "Your FIND Credentials", description: "Set Your FIND Credentials")
    }
    section("Select Your Area Switches"){
      href(name: "toSwitchSetup", page: "switchSetup", title: "Select Your Area Switches", description: "Select Your Area Switches")
    }
  }
}

def credentialsSetup(){
  dynamicPage(name: "credentialsSetup", title: "FIND Credentials Setup", nextPage: "appSetup") {
    section("Your FIND Server"){
      input "findUri", "text", title: "FIND Server", default: "http://ml2.internalpositioning.com/",  description: "Enter in your FIND Server", required: true
    }
    section("Your FIND Group"){
      input "findGroup", "text", title: "FIND Group", description: "Enter in your FIND Group", required: true
    }
    section("Your FIND Group"){
      input "findUser", "text", title: "FIND User", description: "Enter in your FIND User", required: true
    }
  }
}

def switchSetup() {
  dynamicPage(name: "switchSetup", title: "Select Your Area Switches", nextPage: "appSetup") {
	  section("Please Select the Switch for the Bed Location") {
      input "bedSwitch", "capability.switch"
    }
	  section("Please Select the Switch for the Desk Location") {
    	input "deskSwitch", "capability.switch"
    }
	  section("Please Select the Switch for the Bathroom Location") {
    	input "bathroomSwitch", "capability.switch"
    }
	  section("Please Select the Switch for the Kitchen Location") {
    	input "kitchenSwitch", "capability.switch"
    }
  }
}


def updated() {
  log.debug "Updated with settings: ${settings}"
  initialize()
}


def initialize() {
  runEvery1Minute(roomHandler)
}


def whatRoom() {
  def params = [
    uri:  "${findUri}",
    path: 'location',
    contentType: 'application/json',
    query: [group: "${findGroup}", user: "${findUser}"]
	]


  try {
    httpGet(params) { resp ->
      log.debug "response status code: ${resp.status}"
      //log.debug "response data: ${resp.data}"
      log.debug "Data: ${resp.data}"
      //log.debug "response contentType: ${resp.contentType}"
      log.debug "room: ${resp.data.users."${findUser}"[0].location}"
      //def location = resp.data.users."${findUser}"[0].location
      return resp.data.users."${findUser}"[0].location
    }
  } catch (e) {
      log.error "something went wrong: $e"
      return
    }
    //return location
}

def roomHandler() {
  def room = whatRoom()
    log.debug "Reported location is: ${room}"
    if (room == "bed") {
		  deskSwitch.off()
      bathroomSwitch.off()
      kitchenSwitch.off()
    	bedSwitch.on()
    	log.debug "Turning the bed switch on"
	} else if (room == "kitchen") {
    	deskSwitch.off()
      bathroomSwitch.off()
      kitchenSwitch.on()
    	bedSwitch.off()
      log.debug "Turning the kitchen switch on"
	} else if (room == "desk") {
    	deskSwitch.on()
      bathroomSwitch.off()
      kitchenSwitch.off()
    	bedSwitch.off()
      log.debug "Turning the desk switch on"
	} else if (room == "bathroom") {
    	deskSwitch.off()
      bathroomSwitch.off()
      kitchenSwitch.on()
    	bedSwitch.off()
      log.debug "Turning the kitchen switch on"
    }
}
