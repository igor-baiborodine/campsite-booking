#!/usr/bin/env bash
set -e

if [[ "$3" == java* && "$(id -u)" = '0' ]]; then
  echo "Switching user form root to $APP_USER..."
  chown -R campsite:campsite "$APP_HOME"
  exec gosu campsite "$@"
fi

exec "$@"
