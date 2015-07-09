#!/usr/bin/expect -f
#set timeout -1
set timeout 60
spawn /var/lib/irods/packaging/setup_irods.sh


expect {

    -re "iRODS service account name.*:" {
        exp_send "{{ irods_service_account_name }}\n"
        exp_continue
    }

    -re "iRODS service group name.*:" {
        exp_send "{{ irods_service_group_name }}\n"
        exp_continue
    }

    -re "iRODS server's port.*:" {
        exp_send "{{ irods_service_port }}\n"
        exp_continue
    }

    -re "iRODS port range .?begin.*:" {
        exp_send "{{ irods_port_range_begin }} \n"
        exp_continue
    }

    -re "iRODS port range .?end.*:" {
        exp_send "{{ irods_port_range_end }}\n"
        exp_continue
    }

    -re "iRODS Vault directory.*:" {
        exp_send "{{ irods_vault_dir_default }}\n"
        exp_continue
    }

    -re "iRODS server's zone_key.*:" {
        exp_send "{{ irods_zone_key }}\n"
        exp_continue
    }

    -re "iRODS server's negotiation_key.*:" {
        exp_send "{{ irods_negotiation_key }}\n"
        exp_continue
    }

    -re "Control Plane port.*:" {
        exp_send "{{ irods_control_plane_port }}\n"
        exp_continue
    }

    -re "Control Plane key.*:" {
        exp_send "{{ irods_control_plane_key }}\n"
        exp_continue
    }

    -re "Schema Validation Base URI.*:" {
        exp_send "\n"
        exp_continue
    }

    -re "iRODS server's administrator username.*:" {
        exp_send "{{ irods_admin_username }}\n"
        exp_continue
    }

    -re "Please confirm these settings.*:" {
        exp_send "yes\n"
    }

    timeout {
        send_user "\n(A) No match found.\n"
        exit 1
    }
}

expect {

    -re "iCAT server's hostname.*:" {
        exp_send "{{ irods_ies_hostname }}\n"
        exp_continue
    }

    -re "iCAT server's ZoneName.*:" {
        exp_send "{{ irods_zone_name }}\n"
        exp_continue
    }

    -re "Please confirm these settings.*:" {
        exp_send "yes\n"
    }

    timeout {
        send_user "\n(B) No match found.\n"
        exit 1
    }

    eof {
       exit 0
    }
}

expect {

    -re "iCAT server's admin password.*:" {
        exp_send "{{ irods_admin_password }}\n"
        exp_continue
    }

    timeout {
        send_user "\n(B) No match found.\n"
        exit 1
    }

    eof {
       exit 0
    }
}
expect eof