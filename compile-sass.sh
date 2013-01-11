#!/bin/bash

# usage:
# ./compile-sass.sh [scss-input] [css-output]
# - scss-input:  default is "styles.scss"
# - scss-output: default is using the same filename as 
#                scss-input, but instead with .css extension.

# change this to your theme name
THEMENAME="HRO"

# change if your configuration differs from the default Eclipse project
THEMEDIR="web-app/VAADIN/themes"
VAADIN_DIR="web-app/WEB-INF/lib"

if [ -z "$1" ] 
then
	THEMEFILE="styles.scss"
else
	THEMEFILE="$1"
fi;

if [ -z "$2" ]
then
	TARGETFILE=${THEMEFILE/\.scss/\.css}
else
	TARGETFILE="$2"
fi;
	
echo "Compiling, theme: $THEMENAME, source: $THEMEFILE, target: $TARGETFILE"
java -cp $VAADIN_DIR/vaadin-theme-compiler-7*.jar com.vaadin.sass.SassCompiler $THEMEDIR/$THEMENAME/$THEMEFILE $THEMEDIR/$THEMENAME/$TARGETFILE && echo "done!"
