#!/usr/bin/ruby

$stdin.each do |line|
  ARGV.each do |arg|
    field = arg.to_i - 1
    if field >= 0 then
      print line.split[field]
    else
      print arg
    end
  end

  print "\n" unless ARGV.empty?
end
