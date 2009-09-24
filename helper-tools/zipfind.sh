#!/bin/sh

# Finds files in zipfiles. Useful for example when trying to find
# a specific java class file in a bunch of jars

TRUE=1
SHOW_FILES=
DISPLAY_CONTENTS=

usage() {
    E_BADARGS=65
    echo "Usage: `basename $0` [OPTIONS] <filename> <zip file> [<zip file]*"
    echo "Possible options are:"
    echo "-l   display the matched files"
    echo "-d   display the contents of matched files"
    exit $E_BADARGS
}

list() {
    ZIPFILE=$1
    LOOK_FOR=$2
    unzip -l -q "$ZIPFILE"|grep "$LOOK_FOR"
}

while getopts ":ld" Option
do
  case $Option in
    l     ) SHOW_FILES=$TRUE;;
    d     ) DISPLAY_CONTENTS=$TRUE;;
    *     ) usage;;   # DEFAULT
  esac
done
shift $(($OPTIND - 1))

if [ ! -n "$1" ] || [ ! -n "$2" ]; then
    usage
fi  

LOOK_FOR=$1
shift

for i in "$@"; do
    str=`unzip -l -q "$i" 2>/dev/null|grep "$LOOK_FOR"|tr -d ' '`
    if test ${#str} -gt 1; then
        echo $i
        if [ $DISPLAY_CONTENTS ]; then
            SEE_CONTENTS="n"
            FILES=`list "$i" "$LOOK_FOR"|awk '{print $4}'|grep -v '/$'`
            for file in $FILES; do
                read -p "Want to view the file $file? [y/N/quit] " SEE_CONTENTS
                if [ $FILE ] && [ -f $FILE ]; then
                    rm $FILE
                fi


                if [ $SEE_CONTENTS ]; then
                    if [ $SEE_CONTENTS = "quit" ]; then
                        exit
                    elif [ $SEE_CONTENTS = "y" ] || [ $SEE_CONTENTS = "Y" ]; then
                        unzip -q "$i" $file -d "/tmp/$USER"
                        FILE=/tmp/$USER/$file 
                        $EDITOR $FILE
                    fi
                fi
            done
        elif [ $SHOW_FILES ]; then
            echo "--------------------------- MATCHED FILES ---------------------------"
            list "$i" "$LOOK_FOR"
            echo "---------------------------------------------------------------------"
        fi
    fi
done
