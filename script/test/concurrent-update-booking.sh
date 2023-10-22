#!/usr/bin/env bash

set -e

start_date="$1"
end_date="$2"
base_url="$3"

payload=$(< script/test/booking-payload.json sed -e "s/EMAIL/john.smith.1@email.com/g" \
  | sed -e "s/FULL_NAME/John Smith 1/g" \
  | sed -e "s/START_DATE/$start_date/g" \
  | sed -e "s/END_DATE/$end_date/g")
printf "Create payload: %s\n" "$payload"

response=$(curl -X POST -H "Content-Type: application/json" -d "$payload" "$base_url"/v2/booking)
printf "Response: %s\n" "$response"

uuid=$(echo "$response" | sed -En 's/.*"uuid":"([^"]*).*/\1/p')
printf "UUID: %s\n" "$uuid"

payload1=${response//\"campsiteId\"\:1/\"campsiteId\"\:2}
printf "Update payload 1: %s\n" "$payload1"

payload2=${response//\"campsiteId\"\:1/\"campsiteId\"\:3}
printf "Update payload 2: %s\n" "$payload2"

curl -X PUT -H "Content-Type: application/json" -d "$payload1" "$base_url"/v2/booking/"$uuid" & \
  curl -X PUT -H "Content-Type: application/json" -d "$payload2" "$base_url"/v2/booking/"$uuid"

sleep 1
printf "\nConcurrent booking update completed\n"
