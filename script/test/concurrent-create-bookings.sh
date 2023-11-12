#!/usr/bin/env bash

set -e

start_date="$1"
end_date="$2"
base_url="$3"

payload1=$(< script/test/booking-payload.json sed -e "s/EMAIL/john.smith.1@email.com/g" \
  | sed -e "s/FULL_NAME/John Smith 1/g" \
  | sed -e "s/START_DATE/${start_date}/g" \
  | sed -e "s/END_DATE/${end_date}/g")
printf "Create payload 1: %s\n" "$payload1"

payload2=$(< script/test/booking-payload.json sed -e "s/EMAIL/john.smith.2@email.com/g" \
  | sed -e "s/FULL_NAME/John Smith 2/g" \
  | sed -e "s/START_DATE/${start_date}/g" \
  | sed -e "s/END_DATE/${end_date}/g")
printf "Create payload 2: %s\n" "$payload2"

payload3=$(< script/test/booking-payload.json sed -e "s/EMAIL/john.smith.3@email.com/g" \
  | sed -e "s/FULL_NAME/John Smith 3/g" \
  | sed -e "s/START_DATE/${start_date}/g" \
  | sed -e "s/END_DATE/${end_date}/g")
printf "Create payload 3: %s\n" "$payload3"

curl -X POST -H "Content-Type: application/json" -d "$payload1" "$base_url"/api/v2/booking & \
  curl -X POST -H "Content-Type: application/json" -d "$payload2" "$base_url"/api/v2/booking & \
  curl -X POST -H "Content-Type: application/json" -d "$payload3" "$base_url"/api/v2/booking &

sleep 1
printf "\nConcurrent bookings creation completed\n"
