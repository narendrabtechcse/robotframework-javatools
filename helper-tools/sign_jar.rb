#!/usr/bin/ruby

KEYSTORE='~/doc/keystore/robotframework'
STOREPASS='robotframework'
ALIAS='robotframework'

ARGV.each do |arg|
  system "jarsigner -keystore #{KEYSTORE} -storepass #{STOREPASS} #{arg} #{ALIAS}"
end
