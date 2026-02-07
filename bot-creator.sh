#!/bin/bash

ACTION=$1
BRAND_TAG=$2

if [ -z "$ACTION" ] || [ -z "$BRAND_TAG" ]; then
  echo "Usage: ./bot-creator.sh start|stop <brand-tag>"
  echo "Example: ./bot-creator.sh start 222"
  exit 1
fi

case "$BRAND_TAG" in
  222)
    BRAND=222
    PORT=8081
    PROFILE=222-prod
    BOT_CONFIG=bots-222-prod.yml
    ;;
  103)
    BRAND=103
    PORT=8082
    PROFILE=103-prod
    BOT_CONFIG=bots-103-prod.yml
    ;;
  *)
    echo "Unknown brand: $BRAND_TAG"
    echo "Supported brands: 222, 103"
    exit 1
    ;;
esac

case "$ACTION" in
  start)
    echo "Starting bot-creator-$BRAND ..."
    BRAND=$BRAND PORT=$PORT PROFILE=$PROFILE BOT_CONFIG=$BOT_CONFIG docker compose up -d
    ;;
  stop)
    echo "Stopping bot-creator-$BRAND ..."
    BRAND=$BRAND PORT=$PORT PROFILE=$PROFILE BOT_CONFIG=$BOT_CONFIG docker compose down
    ;;
  *)
    echo "Unknown action: $ACTION"
    echo "Usage: ./bot-creator.sh start|stop <brand-tag>"
    exit 1
    ;;
esac