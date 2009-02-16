#!/usr/bin/env python

import os
import sys
from glob import glob

def main(args):
    dir = os.path.dirname(__file__)
    jars = glob(os.path.join(dir, 'test-lib', '*.jar'))
    os.environ['CLASSPATH'] = os.pathsep.join(jars)
    outputdir = os.path.join(dir, 'results')
    os.system("jybot --loglevel TRACE --outputdir %s %s" % (outputdir, ' '.join(args)))

if __name__ == '__main__':
    main(sys.argv[1:])
