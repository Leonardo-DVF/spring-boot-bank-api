#!/usr/bin/env bash
export JAVA_HOME="/c/Users/leonardoferreira/.jdks/ms-17.0.17"
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw "$@"
