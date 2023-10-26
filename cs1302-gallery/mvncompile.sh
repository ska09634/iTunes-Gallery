#!/bin/bash -e

mvn clean
mvn compile
export MAVEN_OPTS=-Dprism.order=sw;
mvn -e exec:java -Dexec.mainClass="cs1302.gallery.GalleryDriver"
