#!/usr/bin/python

import sys, os, subprocess, argparse, stat, logging, grp, pwd

logging.basicConfig(level = logging.INFO)

logger = logging.getLogger(__name__)

class DefaultAction(argparse.Action):
    def __call__(self, parser, namespace, values, option_string=None):
        if not 'ordered_args' in namespace:
            setattr(namespace, 'ordered_args', [])
        previous = namespace.ordered_args
        previous.append((self.dest, values))
        setattr(namespace, 'ordered_args', previous)

def create_check_volume_action(uid):
    class CheckVolume(DefaultAction):
        def __init__(self, option_strings, dest, nargs=None, **kwargs):
            super(CheckVolume, self).__init__(option_strings, dest, **kwargs)
            self.uid = uid
            self.user = pwd.getpwuid(int(uid)).pw_name
    
        def __call__(self, parser, namespace, values, option_string=None):
            if (self.is_volume_valid(values)):
                super(CheckVolume, self).__call__(parser, namespace, values, option_string)

        def is_volume_valid(self, volume):
            vspec = volume.split(":")
            volumepath = vspec[0]
            ro = len(vspec) == 3 and vspec[2] == 'ro'

            st = os.stat(volumepath)
            logger.debug('(uid=%s, volume_path=%s)/(st_uid=%s, st_gid=%s, st_mode=%s)', self.uid, volumepath, st.st_uid, st.st_gid, oct(st.st_mode))
            
            if (os.path.islink(volumepath)):
                volumepath = os.path.realpath(volumepath)
            
            # if user is the owner then OK
            if (int(st.st_uid) == int(self.uid)):
                return True
            
            # if user is a memeber of the group then check RW permissions
            groups = [g.gr_name for g in grp.getgrall() if self.user in g.gr_mem]
            if (st.st_gid in groups):
                return bool(st.st_mode & (stat.S_IRGRP if ro else stat.S_IWGRP))
            
            # check others' permissions
            return bool(st.st_mode & (stat.S_IROTH if ro else stat.S_IWOTH))

    return CheckVolume


IMAGE_AND_COMMAND = 'IMAGE_AND_COMMAND'
CONTAINER = 'CONTAINER'


def get_argparser():
    parser = argparse.ArgumentParser()
    subparsers = parser.add_subparsers(
        title='subcommands', description='valid subcommands', help='additional help')

    parser_stop = subparsers.add_parser('stop')
    parser_stop.add_argument('-t', '--time')
    parser_stop.add_argument(CONTAINER)

    parser_pause = subparsers.add_parser('pause')
    parser_pause.add_argument(CONTAINER)

    parser_unpause = subparsers.add_parser('unpause')
    parser_unpause.add_argument(CONTAINER)

    parser_unpause = subparsers.add_parser('ps')

    parser_unpause = subparsers.add_parser('images')

    parser_unpause = subparsers.add_parser('rm')
    parser_unpause.add_argument(CONTAINER)

    parser_run = subparsers.add_parser('run')
    parser_run.add_argument('-e', '--env', dest='-e', action=DefaultAction)
    parser_run.add_argument('-u', '--user', dest='-u', action=DefaultAction)
    parser_run.add_argument('-w', '--workdir', dest='-w',  action=DefaultAction)
    parser_run.add_argument('-v', '--volume', dest='-v', action=create_check_volume_action(get_uid()))
    parser_run.add_argument('--volumes-from', dest='--volumes-from',  action=DefaultAction)
    parser_run.add_argument('--rm', dest='--rm',  nargs=0, action=DefaultAction)
    parser_run.add_argument('-t', '--tty', dest='-t',  nargs=0, action=DefaultAction)
    parser_run.add_argument('--read-only', dest='--read-only',  nargs=0, action=DefaultAction)
    parser_run.add_argument('-p', '--publish', dest='-p',  action=DefaultAction)
    parser_run.add_argument('-P', '--publish-all', nargs=0, dest='-P',  action=DefaultAction)
    parser_run.add_argument('--name', dest='--name',  action=DefaultAction)
    parser_run.add_argument('--hostname', dest='--hostname',  action=DefaultAction)
    parser_run.add_argument('-m', '--memory', dest='-m',  action=DefaultAction)
    parser_run.add_argument('--memory-swap', dest='--memory-swap',  action=DefaultAction)
    parser_run.add_argument('-d', '--detach', dest='--detach', nargs=0, action=DefaultAction)
    parser_run.add_argument('--add-host', dest='--add-host',  action=DefaultAction)
    parser_run.add_argument('--entrypoint', dest='--entrypoint',  action=DefaultAction)
    parser_run.add_argument('--env-file', dest='--env-file',  action=DefaultAction)
    parser_run.add_argument('-l', '--label', dest='-l',  action=DefaultAction)
    parser_run.add_argument('--label-file', dest='--label-file',  action=DefaultAction)
    parser_run.add_argument('--link', dest='--link',  action=DefaultAction)
    parser_run.add_argument(IMAGE_AND_COMMAND, nargs=argparse.REMAINDER, action=DefaultAction)

    return parser    

def get_username():
    return os.environ.get('SUDO_USER') 

def get_uid():
    return os.environ.get('SUDO_UID') 

def run_command(command):
    try:
        output = subprocess.Popen(command, stderr=subprocess.STDOUT, shell=True)
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
    
    logger.debug('(uid=%s, username=%s)', get_uid(), get_username())

    if (get_uid() == 0):
        exit_err("root doesn't suppose to run this script.")

    logger.debug('(uid=%s, arguments=[ %s ])', get_uid(), " ".join(argv))
    
    if (len(argv) < 1):
        exit_err(" Usage: dickercmd <run|stop|pouse|unpouse> <options>")

    args = get_argparser().parse_args(argv)

    if hasattr(args, 'ordered_args'):
        logger.debug('(uid=%s, filtered_arguments=%s)', get_uid(), args.ordered_args)

        ordered_args = reduce(lambda x, y: x + [y[0]] + (y[1] if isinstance(y[1], list) else [y[1]]), args.ordered_args, [])
        ordered_args = filter(lambda x: x not in [IMAGE_AND_COMMAND, CONTAINER], ordered_args)

        command = ' '.join(['docker', argv[0]] + ordered_args)
        logger.info('(uid=%s, command=[ %s ])', get_uid(), command)
    else:
        command = ' '.join(['docker'] + argv)
    
    run_command(command) 

if __name__ == "__main__":
    main(sys.argv[1:])