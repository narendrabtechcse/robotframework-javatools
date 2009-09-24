#!/bin/sh

find ~/.m2 -name \*.jar|xargs zipfind $*
