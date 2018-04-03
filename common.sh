#!/usr/bin/env bash

set -e

function version {
    cat build.gradle | grep "version[^:]" | grep -o "'.*'" | awk 'BEGIN{FS="'\''"}{print $2}'
}