#!/bin/bash

A=Whydah-CRMService
V=SNAPSHOT


if [[ $V == *SNAPSHOT* ]]; then
   echo Note: If the artifact version contains "SNAPSHOT" - the artifact latest greatest snapshot is downloaded, Irrelevent of version number!!!
   path="http://mvnrepo.cantara.no/content/repositories/snapshots/net/whydah/service/$A"
   version=`curl -s "$path/maven-metadata.xml" | grep "<version>" | sed "s/.*<version>\([^<]*\)<\/version>.*/\1/" | tail -n 1`
   echo "Version $version"
   build=`curl -s "$path/$version/maven-metadata.xml" | grep '<value>' | head -1 | sed "s/.*<value>\([^<]*\)<\/value>.*/\1/"`
   JARFILE="$A-$build.jar"
   url="$path/$version/$JARFILE"
else #A specific Release version
   path="http://mvnrepo.cantara.no/content/repositories/snapshots/net/whydah/service/$A"
   url=$path/$V/$A-$V.jar
   JARFILE=$A-$V.jar
fi

# Download
echo Downloading $url
wget -O $JARFILE -q -N $url


#Create symlink or replace existing sym link
if [ -h $A.jar ]; then
   unlink $A.jar
fi
ln -s $JARFILE $A.jar

# Delete old jar files
jar=$A*.jar
nrOfJarFilesToDelete=`ls $jar -A1t | tail -n +6 | wc -l`
if [[ $nrOfJarFilesToDelete > 0 ]]; then
    echo Deleting $nrOfJarFilesToDelete old jar files. Keep the 4 newest + the symlink.
    ls $jar -A1t | tail -n +6 | xargs rm
fi
