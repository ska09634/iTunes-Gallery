#!/bin/bash -e

javac -d bin -p $JAVAFX_HOME/lib --add-modules javafx.controls src/main/java/cs1302/gallery/*.java
java -Dprism.order=sw -cp bin -p $JAVAFX_HOME/lib --add-modules javafx.controls cs1302.gallery.GalleryDriver
