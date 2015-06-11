#!/usr/bin/expect -f
#set timeout -1
set timeout 60
spawn iinit


expect {

    -re "Enter the host name .DNS. of the server to connect to.*:" {
        exp_send "localhost\n"
        exp_continue
    }

    -re "Enter the port number.*:" {
        exp_send "{{ irods_service_port }}\n"
        exp_continue
    }

    -re "Enter your irods user name.*:" {
        exp_send "{{ irods_admin_username }}\n"
        exp_continue
    }

    -re "Enter your irods zone.*:" {
        exp_send "{{ irods_zone_name }}\n"
        exp_continue
    }

    -re "Enter your current iRODS password.*:" {
        exp_send "{{ irods_admin_password }}\n"
        exp_continue
    }

    timeout {
        send_user "\n(A) No match found.\n"
        exit 1
    }

    eof {
       exit 0
    }
}
expect eof