#!/usr/bin/env ruby

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

FileUtils.mkdir("doc") unless File.directory?("doc")

library = ARGV.first
fail "Usage #{File.basename($0)} <library>" if library.nil?

ENV['CLASSPATH'] = root_dir do |dir|
  unless File.directory?("#{dir}/target/test-classes")
    system "mvn test-compile"
  end

  unless File.directory?("#{dir}/target/dependency")
    system "mvn dependency:copy-dependencies -Dscope=test"
  end

  classpath = Dir["#{dir}/target/dependency/*.jar"] << "#{dir}/target/classes" << "#{dir}/target/test-classes"
  classpath.join(':')
end

system "jython -Dpython.path=/usr/lib/python2.5/site-packages /home/hhulkko/bin/libdoc.py --output doc/#{library.downcase}.html #{library}"
