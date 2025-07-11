#!/bin/bash

docker build -t meassi/kong-auth0-profile:latest ../profile
docker tag meassi/kong-auth0-profile:latest meassi/profile:latest
docker push meassi/kong-auth0-profile:latest
docker push meassi/profile:latest