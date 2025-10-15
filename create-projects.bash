#!/usr/bin/env bash

# Lane Service
spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=lane-service \
--package-name=com.bowling.lane \
--groupId=com.bowling.lane \
--dependencies=web,validation \
--version=1.0.0-SNAPSHOT \
lane-service

# Bowling Ball Service
spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=bowlingball-service \
--package-name=com.bowling.bowlingball \
--groupId=com.bowling.bowlingball \
--dependencies=web,validation \
--version=1.0.0-SNAPSHOT \
bowlingball-service

# Shoe Service
spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=shoe-service \
--package-name=com.bowling.shoe \
--groupId=com.bowling.shoe \
--dependencies=web,validation \
--version=1.0.0-SNAPSHOT \
shoe-service

# API Gateway
spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=api-gateway \
--package-name=com.bowling.apigateway \
--groupId=com.bowling.apigateway \
--dependencies=web,webflux,validation,hateoas \
--version=1.0.0-SNAPSHOT \
api-gateway

# Transaction Service (Placeholder for Milestone 2)
spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=transaction-service \
--package-name=com.bowling.transaction \
--groupId=com.bowling.transaction \
--dependencies=web,validation \
--version=1.0.0-SNAPSHOT \
transaction-service
