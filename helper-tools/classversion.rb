#!/usr/bin/ruby

def get_class_version(file)
  File.open(file) do |f|
    f.seek 6
    f.read(2).unpack("n")
  end
end

puts get_class_version(ARGV.first)
