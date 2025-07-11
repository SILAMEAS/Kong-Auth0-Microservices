#!/bin/bash

docker build -t meassi/kong-auth0-restaurant:latest ../restaurant
docker tag meassi/kong-auth0-restaurant:latest meassi/restaurant:latest
docker push meassi/kong-auth0-restaurant:latest
docker push meassi/restaurant:latest