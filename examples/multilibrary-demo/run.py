#!/usr/bin/env python

import os
import sys
from glob import glob

def main(args):
    dir = os.path.dirname(__file__)
    output = os.path.join(dir, 'results')
    pythonpath = os.path.join(dir, 'tools')
    jars = glob(os.path.join(dir, 'lib', '*.jar'))
    os.environ['CLASSPATH'] = os.pathsep.join(jars)
    cmd = "jybot --pythonpath %s --outputdir %s %s"
    os.system(cmd % (pythonpath, output, ' '.join(args)))

if __name__ == '__main__':
    main(sys.argv[1:])
