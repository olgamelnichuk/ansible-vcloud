#!/bin/sh
# -Djavax.net.debug=all
JAVA_OPTS="-Djdk.tls.client.protocols=TLSv1.2 -Djavax.net.ssl.trustStore=/etc/tomcat7/truststore.jks"