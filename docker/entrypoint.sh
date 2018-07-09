#!/bin/sh


cd /home/dummysmtp/dummy-smtp-server/bin/
bash dummy-smtp-server server app.yaml
echo "[hit enter key to exit] or run 'docker stop <container>'"

read
pause

# stop service and clean up here
echo "exited $0"
