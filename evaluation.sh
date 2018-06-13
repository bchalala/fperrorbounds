#!/bin/bash

for filename in benchmarks/*.java; do
    ant run -Dinput="$filename" -DgenPrecision=genPrecision
done