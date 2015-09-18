#!/usr/bin/env bash
# =========================================================
# Create a symbolic link to irap common directory with references and examples
#
# =========================================================
username=$(whoami)
ln /export/iRAP/ /export/users/$username/iRAP
