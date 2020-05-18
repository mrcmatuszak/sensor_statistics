#!/bin/bash

sensors=$1
count=$2
lower=$3
upper=$4


for i in `seq 1 $count`
do
	s="s$((1 + RANDOM % $sensors))"
	v=$(($lower + RANDOM % ($upper-$lower)))
	echo "$s,$v"
done
