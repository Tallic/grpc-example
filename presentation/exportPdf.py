import os
import glob
import argparse
from subprocess import call
import shutil

assert shutil.which('inkscape'), 'Expected that inkscape command is installed on the system'

parser = argparse.ArgumentParser(description='Exports all found svg\'s as pdf.')
parser.add_argument(
    '-p', '--path',
    type=str,
    help='The root path that is searched.',
    default=os.path.dirname(os.path.realpath(__file__))
)
parser.add_argument(
    '-o', '--overwrite',
    help='Whether existing file should be overwritten.',
    default=False,
    action='store_true'
)

args = parser.parse_args()

path = args.path
if not args.path.endswith('/'):
    path = args.path + '/'
svgs = glob.glob(path + '**/*.svg', recursive=True)

for svg in svgs:
    pre, ext = os.path.splitext(svg)
    pdf = pre + '.png'
    if args.overwrite or not os.path.exists(pdf):
        call(['inkscape', '-D', '-e', pdf, svg])
