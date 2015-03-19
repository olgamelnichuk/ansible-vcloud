#!/usr/bin/expect -f
#set timeout -1
set timeout 60
spawn /var/lib/irods/packaging/setup_irods.sh


expect {

    -re "iRODS service account name.*:" {
        exp_send "irods\n"
        exp_continue
    }

    -re "iRODS service group name.*:" {
        exp_send "irods\n"
        exp_continue
    }

    -re "iRODS server's zone name.*:" {
        exp_send "tempZone\n"
        exp_continue
    }

    -re "iRODS server's port.*:" {
        exp_send "1247\n"
        exp_continue
    }

    -re "iRODS port range .?begin.*:" {
        exp_send "20000\n"
        exp_continue
    }

    -re "iRODS port range .?end.*:" {
        exp_send "20199\n"
        exp_continue
    }

    -re "iRODS Vault directory.*:" {
        exp_send "/var/lib/irods/iRODS/Vault\n"
        exp_continue
    }

    -re "iRODS server's LocalZoneSID.*:" {
        exp_send "TEMP_LOCAL_ZONE_SID\n"
        exp_continue
    }

    -re "iRODS server's agent_key.*:" {
        exp_send "temp_32_byte_key_for_agent__conn\n"
        exp_continue
    }

    -re "iRODS server's administrator username.*:" {
        exp_send "rods\n"
        exp_continue
    }
    -re "iRODS server's administrator password:" {
        exp_send "12345\n"
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
        -re "Database server's hostname or IP address.*:" {
        exp_send "localhost\n"
        exp_continue
        }

    -re "Database server's port.*:" {
        exp_send "5432\n"
        exp_continue
    }

    -re "Database name.*:" {
        exp_send "ICAT\n"
        exp_continue
    }

    -re "Database username.*:" {
        exp_send "irods\n"
        exp_continue
    }

    -re "Database password:" {
        exp_send "testpassword\n"
        exp_continue
    }

    -re "Please confirm these settings.*:" {
        exp_send "yes\n"
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