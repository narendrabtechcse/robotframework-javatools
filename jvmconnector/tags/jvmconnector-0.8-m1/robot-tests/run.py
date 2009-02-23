#!/usr/bin/env python

import os
import sys
from glob import glob

def main(args):
    dir = os.path.dirname(__file__)
    classpath = glob(os.path.join(dir, '..', 'target', 'dependency', '*.jar'))
    classpath.append(os.path.join(dir, '..', 'target', 'test-classes'))
    classpath.append(os.path.join(dir, '..', 'target', 'classes'))
    os.environ['CLASSPATH'] = os.pathsep.join(classpath)
    outputdir = os.path.join(dir, 'results')
    os.system("jybot --loglevel TRACE --outputdir %s %s" % (outputdir, ' '.join(args)))

if __name__ == '__main__':
    main(sys.argv[1:])
