#!/usr/bin/env bash
function copyEnvVarsToGradleProperties {
    LOCAL_PROPERTIES="local.properties"
    export LOCAL_PROPERTIES
    echo "Local Properties should exist at $LOCAL_PROPERTIES"

    if [ ! -f "$LOCAL_PROPERTIES" ]; then
        echo "Local Properties does not exist"

        echo "Creating Local Properties file..."
        touch $LOCAL_PROPERTIES

        echo "Writing keystorePath to local.properties..."
        echo "keystorePath=$keystorePath" >> $LOCAL_PROPERTIES

        echo "Writing keystoreAlias to local.properties..."
        echo "keystoreAlias=$keystoreAlias" >> $LOCAL_PROPERTIES

        echo "Writing keyPass to local.properties..."
        echo "keyPass=$keyPass" >> $LOCAL_PROPERTIES

        echo "Writing storePass to local.properties..."
        echo "storePass=$keyPass" >> $LOCAL_PROPERTIES

        echo "Writing HOST_STAGING to local.properties..."
        echo "HOST_STAGING=\"$HOST_STAGING\"" >> $LOCAL_PROPERTIES

        echo "Writing ADMOB_ID to local.properties..."
        echo "ADMOB_ID=\"$ADMOB_ID\"" >> $LOCAL_PROPERTIES

        echo "Writing ADMOB_UNIT_ID to local.properties..."
        echo "ADMOB_UNIT_ID=\"$ADMOB_UNIT_ID\"" >> $LOCAL_PROPERTIES

        echo "Writing ADMOB_TEST_DEVICE_ID to local.properties..."
        echo "ADMOB_TEST_DEVICE_ID=\"$ADMOB_TEST_DEVICE_ID\"" >> $LOCAL_PROPERTIES

        echo "done"
    fi
}