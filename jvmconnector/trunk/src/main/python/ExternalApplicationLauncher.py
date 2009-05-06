#!/usr/bin/python

import os
from os.path import abspath, dirname, exists, join, normpath
import sys

#JAVA = '/home/rantanen/opt/jdk1.5.0_16//bin/java'

class RMILauncher:

    def __init__(self):
        self.port_info_file = join(dirname(__file__), 'port.txt')

    def launch_java_with_rmi(self, args):
        print args
        command = self._get_command(args)
        print command
        os.popen2(command)

    def _get_command(self, args):
        if 'RMILauncher' in args:
            return '%s %s' % (self._get_java(), ' '.join(args))
        else:
            return  ' '.join( [self._get_java(), 'RMILauncher', 
                               self._get_xml() ] + args) 

    def _get_java(self):
        fake_java_dir = normpath(abspath(dirname(__file__)))
        for path in os.environ['PATH'].split(os.pathsep):
            for java in ['java', 'java.sh', 'java.exe']:
                java_path = normpath(abspath(join(path, java)))
                if exists(java_path) and not fake_java_dir in java_path:
                    print java_path
                    return java_path
    
    def _get_xml(self):
        port = self._get_port()
        xml_path = self._generate_xml(port)
        self._write(self.port_info_file, str(port))
        return xml_path
        
    def _get_port(self):
        if not exists(self.port_info_file):
            return 11100
        f = open(self.port_info_file, 'r')
        content = f.read().strip()
        f.close()
        return int(content) + 1
        
    def _write(self, path, content):
        f = open(path, 'w')
        f.write(content)
        f.close()

    def _generate_xml(self, port):
        content = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN//EN"
        "http://www.springframework.org/dtd/spring-beans.dtd">
<beans>
  <bean id="library" class="SwingLibrary" />

  <bean id="rmiService" class="org.robotframework.jvmconnector.server.SimpleRobotRmiService">
    <property name="library" ref="library" />
  </bean>

  <bean id="serviceExporter" class="org.springframework.remoting.rmi.RmiServiceExporter">
    <property name="serviceName" value="SwingLibrary" />
    <property name="service" ref="rmiService" />
    <property name="registryPort" value="%s" />
    <property name="serviceInterface" value="org.robotframework.jvmconnector.server.RobotRmiService" />
  </bean>
</beans>
""" % (port)
        xml_path = join(dirname(__file__), '..', 'configs', 'config_%s.xml' % (port))
        self._write(xml_path, content)
        return xml_path
        
if __name__ == '__main__':
    launcher = RMILauncher()
    launcher.launch_java_with_rmi(sys.argv[1:])