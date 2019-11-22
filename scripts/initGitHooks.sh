#!/usr/bin/env bash

cp git-hooks/* .git/hooks/ && chmod -R +x .git/hooks
echo "------- GIT HOOKS INITIALIZED! -----------"
