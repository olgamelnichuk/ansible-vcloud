<?php
# Protect against web entry
if ( !defined( 'MEDIAWIKI' ) ) {
	exit;
}
$wgSitename = "****";
$wgMetaNamespace = "****";
$wgScriptPath = "/mediawiki";
$wgScriptExtension = ".php";
$wgServer = "****";
$wgStylePath = "$wgScriptPath/skins";
$wgLogo = "$wgScriptPath/resources/assets/wiki.png";
$wgEnableEmail = false;
$wgEnableUserEmail = false; # UPO
$wgEmergencyContact = "****";
$wgEnotifUserTalk = false; # UPO
$wgEnotifWatchlist = false; # UPO
$wgEmailAuthentication = false;
$wgDBtype = "mysql";
$wgDBserver = "localhost";
$wgDBname = "****";
$wgDBuser = "****";
$wgDBpassword = "****";
$wgDBprefix = "";
$wgDBTableOptions = "ENGINE=InnoDB, DEFAULT CHARSET=binary";
$wgDBmysql5 = true;
$wgMainCacheType = CACHE_NONE;
$wgMemCachedServers = array();
$wgEnableUploads = true;
$wgUseImageMagick = true;
$wgImageMagickConvertCommand = "/usr/bin/convert";
$wgUseInstantCommons = false;
$wgShellLocale = "en_US.utf8";
$wgLanguageCode = "en";
$wgSecretKey = "****";
$wgRightsPage = ""; # Set to the title of a wiki page that describes your license/copyright
$wgRightsUrl = "";
$wgRightsText = "";
$wgRightsIcon = "";
$wgDiff3 = "/usr/bin/diff3";
$wgGroupPermissions['*']['createaccount'] = false;
$wgGroupPermissions['*']['edit'] = false;
#$wgGroupPermissions['*']['read'] = false;
$wgDefaultSkin = "vector";
$wgDebugLogFile = "/var/lib/mediawiki/logs/debug.log";

require_once "$IP/extensions/LdapAuthentication/LdapAuthentication.php";
require_once "$IP/includes/AuthPlugin.php";

$wgLDAPDebug = 1;
$wgDebugLogGroups['ldap'] = '/var/lib/mediawiki/logs/ldapdebug.log';
$wgAuth = new LdapAuthenticationPlugin();
$wgLDAPLowerCaseUsername = array(
  'CloudLDAPServer' => true,
);

$wgLDAPDomainNames = array(
  'CloudLDAPServer',
);
$wgLDAPServerNames = array(
  'CloudLDAPServer' => 'ldap',
);

$wgLDAPEncryptionType = array(
  'CloudLDAPServer' => 'clear',
);

$wgLDAPSearchAttributes = array(
  'CloudLDAPServer' => 'uid'
);
$wgLDAPBaseDNs = array(
  'CloudLDAPServer' => '{{ wiki_ldap_base }}',
);
// Pull LDAP groups a user is in, and update local wiki security group.
// Default: false
$wgLDAPUseLDAPGroups = array(
  'CloudLDAPServer' => true,
);

// Get every group from LDAP, and add it to $wgGroupPermissions. This
// is useful for plugins like Group Based Access Control. This is very
// resource intensive, and probably shouldn't be used in very large
// environments.
// Default: false
$wgLDAPGroupsPrevail = array(
  'CloudLDAPServer' => true,
);

require_once "$IP/skins/CologneBlue/CologneBlue.php";
require_once "$IP/skins/Modern/Modern.php";
require_once "$IP/skins/MonoBook/MonoBook.php";
require_once "$IP/skins/Vector/Vector.php";
require_once "$IP/extensions/Cite/Cite.php";
require_once "$IP/extensions/ConfirmEdit/ConfirmEdit.php";
require_once "$IP/extensions/Gadgets/Gadgets.php";
require_once "$IP/extensions/ImageMap/ImageMap.php";
require_once "$IP/extensions/InputBox/InputBox.php";
require_once "$IP/extensions/Interwiki/Interwiki.php";
require_once "$IP/extensions/LocalisationUpdate/LocalisationUpdate.php";
require_once "$IP/extensions/Nuke/Nuke.php";
require_once "$IP/extensions/ParserFunctions/ParserFunctions.php";
require_once "$IP/extensions/PdfHandler/PdfHandler.php";
require_once "$IP/extensions/Poem/Poem.php";
require_once "$IP/extensions/Renameuser/Renameuser.php";
require_once "$IP/extensions/SpamBlacklist/SpamBlacklist.php";
require_once "$IP/extensions/SyntaxHighlight_GeSHi/SyntaxHighlight_GeSHi.php";
require_once "$IP/extensions/TitleBlacklist/TitleBlacklist.php";
require_once "$IP/extensions/WikiEditor/WikiEditor.php";
require_once "$IP/extensions/WikiForum/WikiForum.php";
