#!/usr/bin/env python

from novaclient import client

import os, sys, time

import argparse, ConfigParser

def str2bool(v):
  return v.lower() in ('yes', 'true')

def get_os_creds():
    try:
        d = {} 
        d['username'] = os.environ['OS_USERNAME']
        d['api_key'] = os.environ['OS_PASSWORD']
        d['project_id'] = os.environ['OS_TENANT_NAME']
        d['auth_url'] = os.environ['OS_AUTH_URL']
    except KeyError as e:
        print >> sys.stderr, "ERROR: environment variable %s is not defined" % e
        sys.exit(-1)
    return d

def get_nova():
    creds = get_os_creds()
    nova = client.Client("2", **creds)
    return nova

def get_free_floating_ips(nova):
    return [x.ip for x in nova.floating_ips.list() if x.fixed_ip is None]

def get_vm_by_name(nova, vm_name):
    lst = [x for x in nova.servers.list() if x.name == vm_name]
    return  lst[0] if lst else None

def create_keypair(nova, vm_keypair):
    if not nova.keypairs.findall(name=vm_keypair):      
        with open(os.path.expanduser("~/.ssh/id_rsa.pub")) as pubkey:
            nova.keypairs.create(name=vm_keypair, public_key=pubkey.read())

def create_vm(nova, vm_name, vm_image, vm_flavor, vm_keypair, vm_meta={}):
    print ("creating '%s'..." % vm_name)
    create_keypair(nova, vm_keypair)

    image = nova.images.find(name=vm_image)
    flavor = nova.flavors.find(name=vm_flavor)

    instance = nova.servers.create(name=vm_name, image=image, flavor=flavor, key_name=vm_keypair, meta=vm_meta)
    
    # Poll at 5 second intervals, until the status is no longer 'BUILD'
    status = instance.status
    while status == 'BUILD':
        time.sleep(5)
        instance = nova.servers.get(instance.id)
        status = instance.status
    print "status: %s" % status
    return instance

def create_vm_command(d):
    nova = get_nova()
    
    vm_name = d['name']
    vm_image = d['image']
    vm_flavor = d['flavor']
    vm_keypair = d['keypair']
    vm_meta = d['meta']
    floating_ip = str2bool(d.get('floating_ip', 'no'))
    
    vm = get_vm_by_name(nova, vm_name)
    if vm is None:
        vm = create_vm(nova, vm_name, vm_image, vm_flavor, vm_keypair, vm_meta) 

    if floating_ip:
        free_ips = get_free_floating_ips(nova)
        if len(free_ips) > 0:
            vm.add_floating_ip(free_ips[0])

def find_command(command):
    return {
        'create': create_vm_command
    }.get(command)

def do_command(command, d):
    print ("do command: %s, %s" % (command, d))

    cmd = find_command(command)
    count = d.get('count', 1)
    for i in range(count):
        cmd(d)

def conf_section_map(config, section):
    d = {}
    options = config.options(section)
    for option in options:
        try:
            d[option] = config.get(section, option)
            if d[option] == -1:
                print ("skip: %s" % option)
        except:
            print ("exception on %s: %s" % (option, sys.exc_info()[1]))
            d[option] = None
    d['meta'] = dict(ansible_group=section)
    return d

def default_config_path():
    return os.path.join(os.path.dirname(os.path.realpath(__file__)), "servers.conf")

def file_exists(parser, arg):
    if os.path.isfile(arg):
        return arg
    parser.error('The file {} does not exist!'.format(arg))


def main(args):
    parser = argparse.ArgumentParser(description='create VMs')
    parser.add_argument('--conf', dest='config', default=default_config_path(), type=lambda x: file_exists(parser, x), help='full path to config')
    subparsers = parser.add_subparsers(help='sub-command help')

    parser_create = subparsers.add_parser('create', help='create vms')
    parser_create.add_argument('section', help='a section name to create VMs from')
    parser_create.set_defaults(command='create')

    args = parser.parse_args()
    
    config = ConfigParser.ConfigParser()
    config.read(args.config)
    
    sections2use = [x for x in config.sections() if x == args.section or args.section == 'all']
    print ("section list: %s" % sections2use)
    for s in sections2use:
        do_command(args.command, conf_section_map(config, s))


if __name__ == "__main__":
    main(sys.argv)