#!/bin/sh

echo "Executing step for generating self signed certificate..."

# Check if the certificate already exists
if [ -f "${SSL_KEY_STORE_PATH}" ]; then
    echo "Certificate already exists at ${SSL_KEY_STORE_PATH}"
else
# Generate a .p12 certificate
openssl req -newkey rsa:2048 -nodes -keyout "${SSL_KEYSTORE_DIR}/audit-log-app.key" \
    -x509 -days 365 -out "${SSL_KEYSTORE_DIR}/audit-log-app.crt" -subj "/CN=Audit Log App"

openssl pkcs12 -export -in "${SSL_KEYSTORE_DIR}/audit-log-app.crt" -inkey "${SSL_KEYSTORE_DIR}/audit-log-app.key" \
    -out "${SSL_KEY_STORE_PATH}" -name ${SSL_KEY_ALIAS} -password pass:${SSL_KEY_STORE_PASS}

echo "PKCS12 keystore generated at ${SSL_KEY_STORE_PATH}"

fi

echo "Executing step for importing open search certificate into java keystore..."
cert_name="root-ca.pem"

# Check if the certificate file exists in the keystore directory
if [ ! -f "${SSL_KEYSTORE_DIR}/$cert_name" ]; then
    echo "Certificate $cert_name not found in ${SSL_KEYSTORE_DIR}"
    exit 1
else
    echo "Certificate $cert_name already exists in ${SSL_KEYSTORE_DIR}"
    # Check if the certificate is already imported into the keystore
    KEYSTORE_ALIAS_EXIST=$(keytool -list -cacerts -storepass "changeit" | grep -w "${OPEN_SEARCH_ALIAS}")

    if [ -z "$KEYSTORE_ALIAS_EXIST" ]; then
        echo "Certificate not found in keystore. Importing it now..."

       # Add the root-ca.pem certificate to the Java truststore
       echo "Adding $cert_name to the truststore..."
       keytool -import -trustcacerts -noprompt --cacerts -storepass "changeit" -alias ${OPEN_SEARCH_ALIAS} -file "${SSL_KEYSTORE_DIR}/$cert_name"

        echo "Certificate imported into keystore with alias ${OPEN_SEARCH_ALIAS}"
    else
        echo "Certificate with alias ${OPEN_SEARCH_ALIAS} already exists in the keystore."
    fi

fi

echo "Starting Java application..."
exec java -jar /app/AuditLog-0.0.1-SNAPSHOT.jar
