#!/bin/bash

# This script must be run from the same directory as the question
# importer jar, since it relies on finding an output/ directory

cd output
dirs="$(ls -1)"
echo "$dirs" > MANIFEST

while read line ; do
  cd $line
  questions="$(ls -1)"
  echo "$questions" > MANIFEST
  cd ..
done < MANIFEST
