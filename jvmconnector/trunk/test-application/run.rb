#!/usr/bin/env ruby

# Before running install Robot Framework and jython
# http://code.google.com/p/robotframework/wiki/Installation

require 'fileutils'
include FileUtils

cd File.dirname(__FILE__)

unless File.directory?('target/classes')
  system 'mvn compile'
end

#classpath = ["#{ENV['HOME']}/doc" ]
classpath = []
classpath << Dir["lib/*.jar"]

ENV['CLASSPATH'] = classpath.join(':')

puts classpath.join(':')
system "jybot --outputdir /tmp --loglevel TRACE #{ARGV.join(' ')}"
