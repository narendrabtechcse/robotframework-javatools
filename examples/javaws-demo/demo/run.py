#!/usr/bin/env python

import os
import sys
from tempfile import gettempdir
from glob import glob

def main(args):
  jars = glob(os.path.join(os.path.dirname(__file__), 'lib', '*.jar'))
  classpath = os.pathsep.join(jars)
  os.environ['CLASSPATH'] = classpath
  os.system("jybot --outputdir %s %s" % (gettempdir(), ' '.join(args)))

if __name__ == '__main__':
  main(sys.argv[1:])
