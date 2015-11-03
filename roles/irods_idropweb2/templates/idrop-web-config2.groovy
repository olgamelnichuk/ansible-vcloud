/*
* Configuration for idrop-web - iDROP Cloud browser
* Project page: https://code.renci.org/gf/project/irodsidrop/
*
* Deployment instructions:
*
* 1) Set configuration for your deployment server. Set the name in production below to the URL you want to use. This value is used by Grails to
* compute links, so it should be the front end for your application (e.g. if you run Apache HTTP server in front of Tomcat with SSL, this would be
* the end-users 'https://' URL
*
*/
environments {
	
	 production {  grails.serverURL = "https://{{ idropweb2_hostname }}:{{ idropweb2_port }}/idrop-web2" } 
	 
}
/*
* 2) If you want to set things up so that the idrop web browser automatically points to a certain grid (i.e. only user ID and password show, then do something like below.
* Take the following outside of the comment block and configure here
*/
idrop.config.preset.host="localhost"
idrop.config.preset.port="{{ irods_service_port }}"
idrop.config.preset.resource=""
idrop.config.preset.zone="{{ irods_zone_name }}"
// can be Standard or PAM right now
idrop.config.preset.authScheme="Standard"
/*
* 3) iDROP web includes the idrop-lite Java applet, which is launched from the iDROP web interface. The interface needs to know where to find this jar file.
* The Jar file should be placed on a web server in an accessible directory, and configured below
*
*
idrop.config.idrop.lite.applet.jar="idrop-lite-1.0.0-SNAPSHOT-jar-with-dependencies.jar"
idrop.config.idrop.lite.codebase="http://iren-web.renci.org/idrop-web/applet"
idrop.config.idrop.lite.use.applet.dir=false
idrop.config.idrop.lite.applet.jar="idrop-lite-1.0.0-SNAPSHOT-jar-with-dependencies.jar"
idrop.config.idrop.lite.codebase="https://lifetime-library.ils.unc.edu/llclient"
idrop.config.idrop.lite.use.applet.dir=false
*/
idrop.config.idrop.lite.applet.jar="idrop-lite-2.0.1-SNAPSHOT-jar-with-dependencies.jar"
idrop.config.idrop.lite.codebase="http://people.renci.org/~danb/FOR_DEMOS/iDrop-Web-2"
idrop.config.idrop.lite.use.applet.dir=false
/*
* 4) iDROP web includes a link to launch the iDROP desktop GUI, using Java WebStart. WebStart looks for a jnlp file and the accompanying
* lib directories. This should be deployed on a web server at some accessible location and referred to here
*
* idrop.config.idrop.jnlp="http://iren-web.renci.org:8080/idrop/idrop.jnlp"
*
* idrop.config.idrop.jnlp="https://lifetime-library.ils.unc.edu/llclient/idrop.jnlp"
*/
//idrop.config.idrop.jnlp="http://iren-web.renci.org:8080/idrop/idrop.jnlp"
//drop.config.idrop.jnlp="http://iren-web.renci.org/idrop-snapshot/idrop.jnlp"
idrop.config.idrop.jnlp="http://people.renci.org/~danb/FOR_DEMOS/iDrop-Web-2/idrop.jnlp"
/*
* 5) Customization properties
*/
// do I support tickets? This determies whether the ticket feature is available via the interface, it also requires ticket support in iRODS itself (version 3.1+)
idrop.config.use.tickets=true
// do I want to display the profile tab and maintain user profile info
idrop.config.use.userprofile=true
// do I support sharing? Requires target server to have specific query support and sharing queries loaded from jargon-user-tagging
idrop.config.use.sharing=true
// do I want to show the gallery view?
idrop.config.use.gallery.view=false
// do I want to show the browse view?
idrop.config.use.browse.view=true


log4j = {

	appenders { 'null' name:'stacktrace' }

	root {
		warn()
		additivity = true
	}

	// Example of changing the log pattern for the default console
	// appender:
	//
	// appenders {
	//     console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
	// }

	error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
			'org.codehaus.groovy.grails.web.pages', //  GSP
			'org.codehaus.groovy.grails.web.sitemesh', //  layouts
			'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
			'org.codehaus.groovy.grails.web.mapping', // URL mapping
			'org.codehaus.groovy.grails.commons', // core / classloading
			'org.codehaus.groovy.grails.plugins', // plugins
			'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
			'org.hibernate',
			'net.sf.ehcache.hibernate'

	//info 'org.irods.mydrop'
	info 'org.irods.jargon'

	warn 'org.irods.jargon.spring.security'
	warn 'org.springframework'
	debug 'grails.app'

	info 'org.mortbay.log',
			'grails.app.controller',
			'org.springframework.security'
}