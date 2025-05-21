Deploying
=========

To deploy to Maven use

```
gradlew clean publish -PcredentialsPassphrase=<credentials password>
```

Where `<credentials password>` is the password used to add the credentials (see 
also below).

To be able to deploy, you need the following:

a `<homedir>/.gradle/gradle.properties` with the following properties:

```
signing.keyId=<gpg key id>
signing.secretKeyRingFile=<path to your secring.gpg> 

centralUsername=<Central Portal usertoken name>
```

In addition, you need to set the following credentials

```
./gradlew addCredentials --key signing.password --value <your secret key password> -PcredentialsPassphrase=<credentials password> 
./gradlew addCredentials --key centralPassword --value <your Central Portal usertoken password> -PcredentialsPassphrase=<credentials password> 
```

See https://github.com/etiennestuder/gradle-credentials-plugin for details on
credentials.
