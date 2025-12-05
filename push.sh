#!/bin/bash
# This is a script that is a shortcut for committing updates to a remote repository

message="${1:-[AUTOMATED COMMIT, DATA UPDATED]}"
git add .
git commit -m "$message"
git push