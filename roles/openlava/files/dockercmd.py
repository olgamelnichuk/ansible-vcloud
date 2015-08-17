#!/usr/bin/python

import sys, os, subprocess, argparse, stat

class DefaultAction(argparse.Action):
    def __call__(self, parser, namespace, values, option_string=None):
        if not 'ordered_args' in namespace:
            setattr(namespace, 'ordered_args', [])
        previous = namespace.ordered_args
        previous.append((self.dest, values))
        setattr(namespace, 'ordered_args', previous)

class CheckVolumes(DefaultAction):
    def __init__(self, option_strings, dest, nargs=None, **kwargs):
        super(CheckVolumes, self).__init__(option_strings, dest, **kwargs)
    
    def __call__(self, parser, namespace, values, option_string=None):
        if (self.volume_valid(values)):
            super(CheckVolumes, self).__call__(parser, namespace, values, option_string)

    def volume_valid(self, volume):
        hvolume = volume.split(":")[0]
        stat = os.stat(hvolume)
        print oct(stat.st_mode)
        print stat.st_uid
        print stat.st_gid
        return True



def get_argparser():
    parser = argparse.ArgumentParser()
    subparsers = parser.add_subparsers(
        title='subcommands', description='valid subcommands', help='additional help')

    parser_stop = subparsers.add_parser('stop')
    parser_stop.add_argument('-t', '--time')
    parser_stop.add_argument('OTHER')

    parser_pause = subparsers.add_parser('pause')
    parser_pause.add_argument('OTHER')

    parser_unpause = subparsers.add_parser('unpause')
    parser_unpause.add_argument('OTHER')

    parser_run = subparsers.add_parser('run')
    parser_run.add_argument('-e', '--env', dest='-e', action=DefaultAction)
    parser_run.add_argument('-u', '--user', dest='-u', action=DefaultAction)
    parser_run.add_argument('-w', '--workdir', dest='-w',  action=DefaultAction)
    parser_run.add_argument('-v', '--volume', dest='-v', action=CheckVolumes)
    parser_run.add_argument('OTHER', nargs=argparse.REMAINDER, action=DefaultAction)

    #parser_create.set_defaults(which='create')
    #parser_create.add_argument(
    #    '--first_name', required=True, help='First Name')
    #parser_create.add_argument(
    #    '--last_name', required=True, help='Last Name'


    return parser    

def check_volume(v):
    print v

def get_username():
    return os.environ.get('SUDO_USER') 

def docker_command(args):
    try:
        command = "docker " + " ".join(args) 
        output = subprocess.check_output(command, stderr=subprocess.STDOUT, shell=True)
        exit_ok(output)
    except subprocess.CalledProcessError as e:
        exit_err(e.output) 

def exit_ok(msg):
    print msg
    sys.exit(0)

def exit_err(msg):
    print >> sys.stderr, msg
    sys.exit(1)

def main(argv):
 
    if (get_username() == 'root'):
        exit_err("root doesn't suppose to run this script.")
    
    if (len(argv) < 1):
        exit_err(" Usage: dickercmd <run|stop|pouse|unpouse> <options>")

    args = get_argparser().parse_args(argv)
    print args.ordered_args
    #docker_command(args)
        
if __name__ == "__main__":
    main(sys.argv[1:])