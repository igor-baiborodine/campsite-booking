#!/usr/bin/env bash

set -e

start_date="$1"
end_date="$2"
base_url="$3"

payload1=$(< booking-payload.json sed -e "s/UUID/$(uuidgen)/g" \
  | sed -e "s/EMAIL/john.smith.1@email.com/g" \
  | sed -e "s/FULL_NAME/John Smith 1/g" \
  | sed -e "s/START_DATE/${start_date}/g" \
  | sed -e "s/END_DATE/${end_date}/g")
echo "Payload 1: $payload1"

payload2=$(< booking-payload.json sed -e "s/UUID/$(uuidgen)/g" \
  | sed -e "s/EMAIL/john.smith.2@email.com/g" \
  | sed -e "s/FULL_NAME/John Smith 2/g" \
  | sed -e "s/START_DATE/${start_date}/g" \
  | sed -e "s/END_DATE/${end_date}/g")
echo "Payload 2: $payload2"

payload3=$(< booking-payload.json sed -e "s/UUID/$(uuidgen)/g" \
  | sed -e "s/EMAIL/john.smith.3@email.com/g" \
  | sed -e "s/FULL_NAME/John Smith 3/g" \
  | sed -e "s/START_DATE/${start_date}/g" \
  | sed -e "s/END_DATE/${end_date}/g")
echo "Payload 3: $payload3"

curl -H "Content-Type: application/json" -d "$payload1" "$base_url"/v1/booking & \
  curl -H "Content-Type: application/json" -d "$payload2" "$base_url"/v1/booking & \
  curl -H "Content-Type: application/json" -d "$payload3" "$base_url"/v1/booking &