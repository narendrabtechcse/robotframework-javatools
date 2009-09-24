#!/usr/bin/env ruby

require 'rubygems'
require 'buildr'

module Buildr
  class Application
    def rakefile
      __FILE__
    end
  end
end

MAX_VERSION = 48

TRANSLATOR =
 'net.sf.retrotranslator:retrotranslator-transformer:jar:1.2.7'

TRANSLATOR_RUNTIME =
 ['net.sf.retrotranslator:retrotranslator-runtime:jar:1.2.7',
  'backport-util-concurrent:backport-util-concurrent:jar:3.1']

repositories.remote << 'http://repo1.maven.org/maven2'

class ClassVersionCheck
  MIN_VERSION = 45

  def initialize(max_version)
    @max_version = max_version
  end

  def assert_classes_have_correct_version(target)
    if File.directory?(target)
      verify_directory(target)
    elsif File.file?(target)
      verify_jar(target)
    end
  end

  def class_versions_ok?(target)
    begin
      assert_classes_have_correct_version(target)
    rescue
      return false
    end
    true
  end

  private

  def verify_directory(dir, src = dir)
    Dir.glob("#{dir}/**/*.class").each do |clss|
      actual_version = get_class_version(clss)
      if actual_version > @max_version.to_i || actual_version < MIN_VERSION
        raise "bad class version: max is #{@max_version} but found #{get_class_version(clss)} from #{clss} in #{src}"
      end
    end
  end

  def verify_jar(jar)
    do_in_tmp_dir do
      system "unzip -qo #{jar}"
      verify_directory('.', jar)
    end
  end

  def get_class_version(file)
    File.open(file) do |f|
      f.seek 6
      ((0xFF & f.getc) << 8) | (0xFF & f.getc)
    end
  end
end

def retro_translate(target, options = {:include_retro => false})
  return if ClassVersionCheck.new(MAX_VERSION).class_versions_ok?(target)
  translator = artifact(TRANSLATOR)
  jars = [translator] + artifacts(TRANSLATOR_RUNTIME)
  do_in_tmp_dir do |tmpdir|
    cp jars.collect {|item| item.invoke; item.to_s}, tmpdir
    translator_jar = File.basename(translator.to_s)
    src = "#{if File.directory? target then "-srcdir" else "-srcjar" end} #{target}"
    sh "java -jar #{translator_jar} #{src}"
  end
  if options[:include_retro] 
    do_in_tmp_dir do |tmpdir|
      artifacts(TRANSLATOR_RUNTIME).each do |jar|
        sh "unzip -qo #{jar}", :verbose => false
      end
      sh "zip -qru #{target} *", :verbose => false
    end
  end
end

def do_in_tmp_dir
  tmp_dir = "/tmp/#{File.basename(__FILE__)}_#{$$}"
  if block_given?
    mkdir_p tmp_dir unless File.directory? tmp_dir
    back = pwd
    cd tmp_dir
    begin
      yield(tmp_dir)
    ensure
      cd back
      rm_rf tmp_dir
    end
  end
  tmp_dir
end

ARGV.each do |jar|
  next unless File.exist?(jar)
  retro_translate(File.expand_path(jar), :include_retro => true)
end
