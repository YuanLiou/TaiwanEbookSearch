#!/usr/bin/env bash
function copyEnvVarsToGradleProperties {
    GRADLE_PROPERTIES="gradle.properties"
    export GRADLE_PROPERTIES
    echo "Gradle Properties should exist at $GRADLE_PROPERTIES"
    
    if [ ! -f "$GRADLE_PROPERTIES" ]; then
        echo "Gradle Properties does not exist"

        echo "Creating Gradle Properties file..."
        touch $GRADLE_PROPERTIES

        echo "Writing keystorePath to gradle.properties..."
        echo "keystorePath=$keystorePath" >> $GRADLE_PROPERTIES

        echo "Writing keystoreAlias to gradle.properties..."
        echo "keystoreAlias=$keystoreAlias" >> $GRADLE_PROPERTIES

        echo "Writing keyPass to gradle.properties..."
        echo "keyPass=$keyPass" >> $GRADLE_PROPERTIES

        echo "Writing storePass to gradle.properties..."
        echo "storePass=$keyPass" >> $GRADLE_PROPERTIES

        echo "Writing HOST to gradle.properties..."
        echo "HOST=\"$HOST\"" >> $GRADLE_PROPERTIES

        echo "Writing ADMOB_ID to gradle.properties..."
        echo "ADMOB_ID=\"$ADMOB_ID\"" >> $GRADLE_PROPERTIES

        echo "Writing ADMOB_UNIT_ID to gradle.properties..."
        echo "ADMOB_UNIT_ID=\"$ADMOB_UNIT_ID\"" >> $GRADLE_PROPERTIES

        echo "done"
    fi
}