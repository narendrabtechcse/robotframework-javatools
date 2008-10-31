#!/usr/bin/ruby

require 'fileutils'

def find_root(file)
  if File.exist?("#{Dir.pwd}/#{file}")
    "#{Dir.pwd}/#{file}"
  else
    find_root("../#{file}")
  end
end

def root_dir
  this_dir = Dir.pwd
  Dir.chdir(File.dirname(find_root('pom.xml')))
  retval = yield(Dir.pwd)
  Dir.chdir(this_dir)
  retval
end

ENV['CLASSPATH'] = root_dir do |dir|
  unless File.exist?("#{dir}/target/.assembly")
    system "mvn assembly:assembly -DdescriptorId=jar-with-dependencies"
    FileUtils.touch("#{dir}/target/.assembly")
  end

  classpath = Dir["#{dir}/target/*.jar"] << "#{dir}/target/test-classes"
  classpath.join(':')
end

system "jybot --outputdir /tmp --critical regression #{ARGV.join(' ')}"
