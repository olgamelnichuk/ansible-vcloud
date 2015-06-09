A collection of ansible scripts to setup VMs on a vcloud (VMware vCloud Director 5.1)...


### Create a vApp:
```shell
ansible-playbook -vvvv -i hosts -c ssh -e@./secret.yml -e@./create_vapp_vars_customized.yml create_vapp.yml --ask-vault-pass

```
### secret.yml example
```YAML
api_host: https://_your_vcloud_api_host_
api_user: _api_username_
api_password: _api_password_
```

### ~/.ssh/config example
```
Host bastion
    HostName _bastion_hostname_
    Port _bastion_port_

Host 192.168.*
    ProxyCommand ssh -A ansible@bastion -W %h:%p
```

### setup ssh key on a vm example
```shell
ansible-playbook -i hosts -c ssh setup_ssh_key.yml --extra-vars "hosts=... key_tag=vm" --ask-pass
```
or
```shell
ansible-playbook -i hosts -c ssh setup_ssh_key.yml --extra-vars "hosts=... ansible_ssh_port=... key_tag=bastion" --ask-pass
```
