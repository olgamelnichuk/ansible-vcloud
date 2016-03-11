#!/usr/bin/env python

from novaclient import client

import os, sys, time

def get_nova_creds():
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

def create_vm(vm_name, vm_image, vm_flavor, vm_keypair):
    creds = get_nova_creds()
    nova = client.Client("2", **creds)
    
    if not nova.keypairs.findall(name=vm_keypair):      
        with open(os.path.expanduser("~/.ssh/id_rsa.pub")) as pubkey:
            nova.keypairs.create(name=vm_keypair, public_key=pubkey.read())

    image = nova.images.find(name=vm_image)
    flavor = nova.flavors.find(name=vm_flavor)

    instance = nova.servers.create(name=vm_name, image=image, flavor=flavor, key_name=vm_keypair)
    
    # Poll at 5 second intervals, until the status is no longer 'BUILD'
    status = instance.status
    while status == 'BUILD':
        time.sleep(5)
        instance = nova.servers.get(instance.id)
        status = instance.status
    print "status: %s" % status


def main(args):
    create_vm("test", "Ubuntu 15.10", "s1.tiny", "mykey")


if __name__ == "__main__":
    main(sys.argv)