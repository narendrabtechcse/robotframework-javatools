#!/usr/bin/env python

import os
import sys
from glob import glob

def main(args):
    dir = os.path.dirname(__file__)
    testlibdir = os.path.join(dir, 'test-lib')
    jars = glob(os.path.join(testlibdir, '*.jar'))
    os.environ['CLASSPATH'] = os.pathsep.join(jars)
    if os.name == 'nt' and not os.getenv('HOME'):
        os.environ['HOME'] = os.environ['USERPROFILE'] 
    outputdir = os.path.join(dir, 'results')
    os.system('jybot --pythonpath "%s" --loglevel TRACE --outputdir "%s" %s' %
    (testlibdir, outputdir, ' '.join(args)))

if __name__ == '__main__':
    main(sys.argv[1:])
