#!/usr/bin/ruby

path = File.dirname(File.readlink(__FILE__))
KEYSTORE="#{path}/keystore/robotframework"
STOREPASS='robotframework'
ALIAS='robotframework'

ARGV.each do |arg|
  system "jarsigner -keystore #{KEYSTORE} -storepass #{STOREPASS} #{arg} #{ALIAS}"
end
