#!/bin/bash

set -o verbose
mvn clean assembly:assembly
jarjar.sh