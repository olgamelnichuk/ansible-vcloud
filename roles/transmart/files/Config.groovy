/*
 * NOTE
 * ----
 * This configuration assumes that the development environment will be used with
 * run-app and the production environment will be used with the application
 * packaged as a WAR and deployed to tomcat. Running grails run-war or otherwise
 * running a WAR with the development profile set up or activating the
 * production environment when running grails run-app are scenarios that have
 * NOT been tested.
 */


// if running as a WAR, we need these
def catalinaBase      = System.getProperty('catalina.base') ?: '.'

def explodedWarDir    = catalinaBase + '/webapps/transmart'
def solrPort          = 8080 //port of appserver where solr runs (under ctx path /solr)
def searchIndex       = catalinaBase + '/searchIndex' //create this directory
// for running transmart as WAR, create this directory and then create an alias
def jobsDirectory     = "/mnt/nfs/rcloud/storage/tempdir/transmart/"
//def jobsDirectory     = "/var/tmp/jobs/"
def useRCloud         = true
def oauthEnabled      = true
def samlEnabled       = false
def gwavaEnabled      = false
def transmartURL      = "http://localhost:${System.getProperty('server.port', '8080')}/transmart/"

//Disabling/Enabling UI tabs
ui {
    tabs {
        //Search was not part of 1.2. It's not working properly. You need to set `show` to `true` to see it on UI
        search.show = false
        browse.hide = false
        //Note: analyze tab is always shown
        sampleExplorer.hide = false
        geneSignature.hide = false
        gwas.hide = false
        uploadData.hide = false
    }
}

// I001 – Insertion point 'post-WAR-variables'

//transmartURL      = 'http://example.com/transmart/'
//oauthEnabled      = true
//samlEnabled       = false
//gwavaEnabled      = false

/* Other things you may want to change:
 * – Log4j configuration
 * – 'Personalization & login' section
 * – Location of Solr instance in 'Faceted Search Configuration'
 * – For enabling SAML, editing the corresponding section is mandatory
 */

/* If you want to be able to regenerate this file easily, instead of editing
 * the generated file directly, create a Config-extra.groovy file in the root of
 * the transmart-data checkout. That file will be appended to this one whenever
 * the Config.groovy target is run */

environments { production {
    if (transmartURL.startsWith('http://localhost:')) {
        println "[WARN] transmartURL not overriden. Some settings (e.g. help page) may be wrong"
    }
} }

/* {{{ Log4J Configuration */
log4j = {
    environments {
        development {
            root {
                info 'stdout'
            }

            // for a less verbose startup & shutdown
            warn  'org.codehaus.groovy.grails.commons.spring'
            warn  'org.codehaus.groovy.grails.orm.hibernate.cfg'
            warn  'org.codehaus.groovy.grails.domain.GrailsDomainClassCleaner'

            debug 'org.transmartproject'
            debug 'com.recomdata'
            debug 'grails.app.services.com.recomdata'
            debug 'grails.app.services.org.transmartproject'
            debug 'grails.app.controllers.com.recomdata'
            debug 'grails.app.controllers.org.transmartproject'
            debug 'grails.app.domain.com.recomdata'
            debug 'grails.app.domain.org.transmartproject'
            // debug 'org.springframework.security'
            // (very verbose) debug  'org.grails.plugin.resource'
        }

        production {
            def logDirectory = "${catalinaBase}/logs".toString()
            appenders {
                rollingFile(name: 'transmart',
                            file: "${logDirectory}/transmart.log",
                            layout: pattern(conversionPattern: '%d{dd-MM-yyyy HH:mm:ss,SSS} %5p %c{1} - %m%n'),
                            maxFileSize: '100MB')
            }
            root {
                info 'transmart'
            }
        }
    }
}
/* }}} */

/* {{{ Faceted Search Configuration */
environments {
    development {
        com.rwg.solr.scheme = 'http'
        com.rwg.solr.host   = 'localhost:8983'
        com.rwg.solr.path   = '/solr/rwg/select/'
    }

    production {
        com.rwg.solr.scheme = 'http'
        com.rwg.solr.host   = 'localhost:' + solrPort
        com.rwg.solr.path   = '/solr/rwg/select/'
    }
}
/* }}} */

/* {{{ Personalization */
// application logo to be used in the login page
com.recomdata.largeLogo = "transmartlogo.jpg"

// application logo to be used in the search page
com.recomdata.searchtool.smallLogo="transmartlogosmall.jpg"

// contact email address
com.recomdata.contactUs = "mailto:transmart-discuss@googlegroups.com"

// application title
com.recomdata.appTitle = "tranSMART v" + org.transmart.originalConfigBinding.appVersion

// Location of the help pages
// Currently, these are distribution with transmart, so it can also point to
// that location copy. Should be an absolute URL
com.recomdata.adminHelpURL = "$transmartURL/help/adminHelp/default.htm"

environments { development {
    com.recomdata.bugreportURL = 'https://jira.transmartfoundation.org'
} }

// Keys without defaults (see Config-extra.php.sample):
// com.recomdata.projectName
// com.recomdata.providerName
// com.recomdata.providerURL
/* }}} */

/* {{{ Login */
// Session timeout and heartbeat frequency (ping interval)
com.recomdata.sessionTimeout = 300
com.recomdata.heartbeatLaps = 30

environments { development {
    com.recomdata.sessionTimeout = Integer.MAX_VALUE / 1000 as int /* ~24 days */
    com.recomdata.heartbeatLaps = 900
} }

// Not enabled by default (see Config-extra.php.sample)
//com.recomdata.passwordstrength.pattern
//com.recomdata.passwordstrength.description

// Whether to enable guest auto login.
// If it's enabled no login is required to access tranSMART.
com.recomdata.guestAutoLogin = false
environments { development { com.recomdata.guestAutoLogin = true } }

// Guest account user name – if guestAutoLogin is true, this is the username of
// the account that tranSMART will automatically authenticate users as. This will
// control the level of access anonymous users will have (the access will be match
// that of the account specified here).
com.recomdata.guestUserName = 'guest'
/* }}} */

/* {{{ Search tool configuration */

// Lucene index location for documentation search
com.recomdata.searchengine.index = searchIndex

/* see also com.recomdata.searchtool.smallogo in the personalization section */
/* }}} */

/* {{{ Sample Explorer configuration */

// This is an object to dictate the names and 'pretty names' of the SOLR fields.
// Optionally you can set the width of each of the columns when rendered.

sampleExplorer {
    fieldMapping = [
        columns:[
            [header:'Sample ID',dataIndex:'id', mainTerm: false, showInGrid: false],
            [header:'BioBank', dataIndex:'BioBank', mainTerm: true, showInGrid: true, width:10],
            [header:'Source Organism', dataIndex:'Source_Organism', mainTerm: true, showInGrid: true, width:10]
            // Continue as you have fields
        ]
    ]
    resultsGridHeight = 100
    resultsGridWidth = 100
    idfield = 'id'
}

edu.harvard.transmart.sampleBreakdownMap = [
    "id":"Aliquots in Cohort"
]

// Solr configuration for the Sample Explorer
com { recomdata { solr {
    maxNewsStories = 10
    maxRows = 10000
}}}

/* }}} */

/* {{{ Dataset Explorer configuration */
com { recomdata { datasetExplorer {
    // set to 'true' (quotes included) to enable gene pattern integration
    genePatternEnabled = 'false'
    // The tomcat URL that gene pattern is deployed within -usually it's proxyed through apache
    genePatternURL = 'http://23.23.185.167'
    // Gene Pattern real URL with port number
    genePatternRealURLBehindProxy = 'http://23.23.185.167:8080'
    // default Gene pattern user to start up - each tranSMART user will need a separate user account to be created in Gene Pattern
    genePatternUser = 'biomart'

    // Absolute path to PLINK executables
    plinkExcutable = '/usr/local/bin/plink'
} } }
// Metadata view
com.recomdata.view.studyview = 'studydetail'

com.recomdata.plugins.resultSize = 5000
/* }}} */

/* {{{ RModules & Data Export Configuration */
environments {
    // defines whether R Cloud should be used instead of RServe
    RModules.useRCloud = useRCloud

    RModules.RCloud.naming.mode = "db"
    RModules.RCloud.db.type = "postgresql"
    RModules.RCloud.db.name = "rcloudemif"
    RModules.RCloud.db.host = "rcloud-01"
    RModules.RCloud.db.port = "5432"
    RModules.RCloud.db.user = "rcloud"
    RModules.RCloud.db.password = "rcloud11223344"

    RModules.RCloud.pools.dbmode.type = "postgresql"
    RModules.RCloud.pools.dbmode.name = "rcloudemif"
    RModules.RCloud.pools.dbmode.host = "rcloud-01"
    RModules.RCloud.pools.dbmode.port = "5432"
    RModules.RCloud.pools.dbmode.user = "rcloud"
    RModules.RCloud.pools.dbmode.password = "rcloud11223344"
    RModules.RCloud.pools.dbmode.defaultpool = "EMIF_4G"


    // This is to target a remove Rserv. Bear in mind the need for shared network storage
    RModules.host = "127.0.0.1"
    RModules.port = 6311

    // This is not used in recent versions; the URL is always /analysisFiles/
    RModules.imageURL = "/tempImages/" //must end and start with /

    production {
        // The working direcotry for R scripts, where the jobs get created and
        // output files get generated
        RModules.tempFolderDirectory = jobsDirectory

        // Whether to copy the images from the jobs directory to another
        // directory from which they can be served. Should be false for
        // performance reasons. Files will be served from the
        // tempFolderDirectory instead, which should be exposed as
        // <context path>/analysisFiles (formerly: <context path>/tempImages)
        RModules.transferImageFile = false

        // Copy inside the exploded WAR. In actual production, we don't want this
        // The web server should be able to serve static files from this
        // directory via the logical name specified in the imageUrl config entry
        // Not needed because transferImageFile is false
        //Rmodules.temporaryImageFolder = explodedWarDir + '/images/tempImages/'
    }
    development {
        RModules.tempFolderDirectory = "/tmp"

        // we have stuff in _Events.groovy to make available the contens in
        // the tempFolderDirectory
        RModules.transferImageFile = false

        /* we don't need to specify temporaryImageDirectory, because we're not copying */
    }

    // Used to access R jobs parent directory outside RModules (e.g. data export)
    com.recomdata.plugins.tempFolderDirectory = RModules.tempFolderDirectory
}
/* }}} */

/* {{{ Misc Configuration */

// This can be used to debug JavaScript callbacks in the dataset explorer in
// Chrome. Unfortunately, it also sometimes causes chrome to segfault
com.recomdata.debug.jsCallbacks = 'false'

environments {
    production {
        com.recomdata.debug.jsCallbacks = 'false'
    }
}

grails.resources.adhoc.excludes = [ '/images' + RModules.imageURL + '**' ]

// Adding properties to the Build information panel
buildInfo { properties {
   include = [ 'app.grails.version', 'build.groovy' ]
   exclude = [ 'env.proc.cores' ]
} }

/* }}} */

/* {{{ Spring Security configuration */

grails { plugin { springsecurity {
    // You probably won't want to change these

    // customized user GORM class
    userLookup.userDomainClassName = 'org.transmart.searchapp.AuthUser'
    // customized password field
    userLookup.passwordPropertyName = 'passwd'
    // customized user /role join GORM class
    userLookup.authorityJoinClassName = 'org.transmart.searchapp.AuthUser'
    // customized role GORM class
    authority.className = 'org.transmart.searchapp.Role'
    // request map GORM class name - request map is stored in the db
    requestMap.className = 'org.transmart.searchapp.Requestmap'
    // requestmap in db
    securityConfigType = grails.plugin.springsecurity.SecurityConfigType.Requestmap
    // url to redirect after login in
    successHandler.defaultTargetUrl = '/userLanding'
    // logout url
    logout.afterLogoutUrl = '/login/forceAuth'

    // configurable requestmap functionality in transmart is deprecated
    def useRequestMap = false

    if (useRequestMap) {
        // requestmap in db
        securityConfigType = 'Requestmap'
        // request map GORM class name - request map is stored in the db
        requestMap.className = 'org.transmart.searchapp.Requestmap'
    } else {
        securityConfigType = 'InterceptUrlMap'
        def oauthEndpoints = [
            '/oauth/authorize.dispatch'   : ['IS_AUTHENTICATED_REMEMBERED'],
            '/oauth/token.dispatch'       : ['IS_AUTHENTICATED_REMEMBERED'],
        ]
        interceptUrlMap = [
            '/login/**'                   : ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/css/**'                     : ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/js/**'                      : ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/grails-errorhandler'        : ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/images/analysisFiles/**'    : ['IS_AUTHENTICATED_REMEMBERED'],
            '/images/**'                  : ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/static/**'                  : ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/search/loadAJAX**'          : ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/analysis/getGenePatternFile': ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/analysis/getTestFile'       : ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/requestmap/**'              : ['ROLE_ADMIN'],
            '/role/**'                    : ['ROLE_ADMIN'],
            '/authUser/**'                : ['ROLE_ADMIN'],
            '/secureObject/**'            : ['ROLE_ADMIN'],
            '/accessLog/**'               : ['ROLE_ADMIN'],
            '/authUserSecureAccess/**'    : ['ROLE_ADMIN'],
            '/secureObjectPath/**'        : ['ROLE_ADMIN'],
            '/userGroup/**'               : ['ROLE_ADMIN'],
            '/secureObjectAccess/**'      : ['ROLE_ADMIN'],
            *                             : (oauthEnabled ?  oauthEndpoints : [:]),
            '/**'                         : ['IS_AUTHENTICATED_REMEMBERED'], // must be last
        ]
        rejectIfNoRule = true
    }

    // Hash algorithm
    password.algorithm = 'bcrypt'
    // Number of bcrypt rounds
    password.bcrypt.logrounds = 14

    providerNames = [
        'daoAuthenticationProvider',
        'anonymousAuthenticationProvider',
        'rememberMeAuthenticationProvider',
    ]

    if (oauthEnabled) {
        providerNames << 'clientCredentialsAuthenticationProvider'

        oauthProvider {
            clients = [
                    [clientId: 'api-client', clientSecret: 'api-client']
            ]
        }
    }

} } }
/* }}} */

//{{{ SAML Configuration

if (samlEnabled) {
    // don't do assignment to grails.plugin.springsecurity.providerNames
    // see GRAILS-11730
    grails { plugin { springsecurity {
        providerNames << 'samlAuthenticationProvider'
    } } }
    // again, because of GRAILS-11730
    def ourPluginConfig
    grails {
        ourPluginConfig = plugin
    }

    org { transmart { security {
        setProperty('samlEnabled', true) // clashes with local variable
        ssoEnabled  = "true"

        // URL to redirect to after successful authentication
        successRedirectHandler.defaultTargetUrl = ourPluginConfig.springsecurity.successHandler.defaultTargetUrl
        // URL to redirect to after successful logout
        successLogoutHandler.defaultTargetUrl = ourPluginConfig.springsecurity.logout.afterLogoutUrl

        saml {
            /* {{{ Service provider details (we) */
            sp {
                // ID of the Service Provider
                id = "gustavo-transmart"

                // URL of the service provider. This should be autodected, but it isn't
                url = "http://localhost:8080/transmart"

                // Alias of the Service Provider
                alias = "transmart"

                // Alias of the Service Provider's signing key, see keystore details
                signingKeyAlias = "saml-signing"
                // Alias of the Service Provider's encryption key
                encryptionKeyAlias = "saml-encryption"
            }
            /* }}} */

            // Metadata file of the provider. We insist on keeping instead of just
            // retrieving it from the provider on startup to prevent transmart from
            // being unable to start due to provider being down. A copy will still be
            // periodically fetched from the provider
            idp.metadataFile = '/home/glopes/idp-local-metadata.xml'

            /* {{{ Keystore details */
            keystore {
                // Generate with:
                //  keytool -genkey -keyalg RSA -alias saml-{signing,encryption} \
                //    -keystore transmart.jks -storepass changeit \
                //    -validity 3602 -keysize 2048
                // Location of the keystore. You can use other schemes, like classpath:resource/samlKeystore.jks
                file = 'file:///home/glopes/transmart.jks'

                // keystore's storepass
                password="changeit"

                // keystore's default key
                defaultKey="saml-signing"

                // Alias of the encryption key in the keystore
                encryptionKey.alias="saml-encryption"
                // Password of that the key with above alis in the keystore
                encryptionKey.password="changeit"

                // Alias of the signing key in the keystore
                signingKey.alias="saml-signing"
                // Password of that the key with above alis in the keystore
                signingKey.password="changeit"
            }
            /* }}} */

            /* {{{ Creation of new users */
            createInexistentUsers = "true"
            attribute.username    = "urn:custodix:ciam:1.0:principal:username"
            attribute.firstName   = "urn:oid:2.5.4.42"
            attribute.lastName    = "urn:oid:2.5.4.4"
            attribute.email       = ""
            attribute.federatedId = "personPrincipalName"
            /* }}} */

            //
            // Except maybe for the binding, you probably won't want to change the rest:
            //

            // Suffix of the login filter, saml authentication is initiated when user browses to this url
            entryPoint.filterProcesses = "/saml/login"
            // SAML Binding to be used for above entry point url.
            entryPoint.binding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"
            // This property must be set otherwise the default binding is used which, in this configuration, is HTTP-ARTIFACT
            entryPoint.defaultAssertionConsumerIndex = "1"

            // Suffix of the Service Provider's metadata, this url needs to be configured on IDP
            metadata.filterSuffix = "/saml/metadata"
            // Id of the spring security's authentication manager
            authenticationManager = "authenticationManager"
            // Whether sessions should be invalidated after logout
            logout.invalidateHttpSession = "true"
            // Id of the spring security user service that should be called to fetch users.
            saml.userService = "org.transmart.FederatedUserDetailsService"
        }
    } } }
} else { // if (!samlEnabled)
    org { transmart { security {
        setProperty('samlEnabled', false) // clashes with local variable
    } } }
}
/* }}} */

/* {{{ gwava */
if (gwavaEnabled) {
    com.recomdata.rwg.webstart.codebase      = "$transmartURL/gwava"
    com.recomdata.rwg.webstart.jar           = './ManhattanViz2.1g.jar'
    com.recomdata.rwg.webstart.mainClass     = 'com.pfizer.mrbt.genomics.Driver'
    com.recomdata.rwg.webstart.gwavaInstance = 'transmartstg'
    com.recomdata.rwg.webstart.transmart.url = "$transmartURL/transmart"
}
/* }}} */

/* {{{ Quartz jobs configuration */
// start delay for the sweep job
com.recomdata.export.jobs.sweep.startDelay =60000 // d*h*m*s*1000
// repeat interval for the sweep job
com.recomdata.export.jobs.sweep.repeatInterval = 86400000 // d*h*m*s*1000
// specify the age of files to be deleted (in days)
com.recomdata.export.jobs.sweep.fileAge = 3
/* }}} */

/* {{{ File store and indexing configuration */
com.rwg.solr.browse.path   = '/solr/browse/select/'
com.rwg.solr.update.path = '/solr/browse/dataimport/'
com.recomdata.solr.baseURL = "${com.rwg.solr.scheme}://${com.rwg.solr.host}" +
                             "${new File(com.rwg.solr.browse.path).parent}"

def fileStoreDirectory = new File(System.getenv('HOME'), '.grails/transmart-filestore')
def fileImportDirectory = new File(System.getProperty("java.io.tmpdir"), 'transmart-fileimport')
com.recomdata.FmFolderService.filestoreDirectory = fileStoreDirectory.absolutePath
com.recomdata.FmFolderService.importDirectory = fileImportDirectory.absolutePath

[fileStoreDirectory, fileImportDirectory].each {
    if (!it.exists()) {
        it.mkdir()
    }
}
/* }}} */

// I002 – Insertion point 'end'


// {{{ Personalization
// Project name shown on the welcome page
//com.recomdata.projectName = "MyProject"

// name and URL of the supporter entity shown on the welcome page
//com.recomdata.providerName = "tranSMART Foundation"
//com.recomdata.providerURL = "http://www.transmartfoundation.org"

// Contact e-mail
//com.recomdata.contactUs = "mailto:support@mycompany.com"
// }}}

// Password strength criteria, please change description accordingly
//com.recomdata.passwordstrength.pattern = ~/^.*(?=.{8,})(?=.*[a-z])(?=.*[A-Z])(?=.*[\d])(?=.*[\W]).*$/

// Password strength regex that is used to test user's passwords that are entered by themselves.
// Take care to change org.transmartproject.app.user.ChangePasswordCommand.newPassword.lowPasswordStrength
// variable inside messages.properties file of web app.
/*
user.password.strength.regex = '''(?x)
^
(?=.*[A-Z])      #Ensure string has an uppercase letter.
(?=.*[!@\#$&*]) #Ensure string has one special case letter.
(?=.*[0-9])      #Ensure string has a digit.
.{8,}            #Ensure string length is not less then 8.
$'''
*/

//Alternative color scheme for aCGH BED tracks.
//Current colors are captured from cghCall R package.
//Although do not correspond exactly.
/*
dataExport {
    bed {
        acgh {
            rgbColorScheme {
                //white
                invalid       = [255, 255, 255]
                //red
                loss          = [205,   0,   0]
                //dark
                normal        = [ 10,  10,  10]
                //green
                gain          = [  0, 255,   0]
                //dark green
                amplification = [  0, 100,   0]
            }
        }
    }
}
*/

// Password strength description, please change according to pattern
//com.recomdata.passwordstrength.description =
//    'It should contain a minimum of 8 characters including at least ' +
//    '1 upper and 1 lower case letter, 1 digit and 1 special character.'

// You MUST leave this at the end
// Do not move it up, otherwise syntax errors may not be detected

org.transmart.configFine = true

// vim: set fdm=marker et ts=4 sw=4 filetype=groovy ai:
