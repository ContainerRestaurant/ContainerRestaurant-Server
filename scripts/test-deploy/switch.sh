#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

function switch_proxy() {
  IDLE_PORT=$(find_idle_port)

  echo "> 전환할 Port: $IDLE_PORT"
  echo "> Port 전환"
  echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" | sudo tee /etc/nginx/conf.d/service-url.inc

  echo "> 엔진엑스 Reload"
  sudo service nginx reload
  sleep 5

  # 현재 프리티어에서 두 개의 서버를 띄워놓으면 메모리를 너무 많이 먹어 동작이 안되는 경우가 있어 기존 서버를 종료한다.
  echo "> 기존에 동작하던 서버를 종료합니다."

  if [ ${IDLE_PORT} == 8081 ]
  then
    NEW_IDLE_PORT=8082
  else
    NEW_IDLE_PORT=8081
  fi
  echo "> 종료할 서버의 포트: $NEW_IDLE_PORT"

  NEW_IDLE_PID=$(lsof -ti tcp:${NEW_IDLE_PORT})
  echo "> kill -15"
  kill -15 $NEW_IDLE_PID
}