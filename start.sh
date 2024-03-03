#!/bin/bash

# member-api image build
# github action 으로 gradle build, 이미지 빌드 후
# docker hub로 push 하도록 변경해야 함
cd api/member
gradle clean build -x test
cd ../..
docker build -t bagtothefuture-member:0.1.0 -f api/member/Dockerfile .

# swagger image build
# github action 으로 이미지 빌드 후,
# docker hub로 push 하도록 변경해야 함
cd docs/
docker build -t bagtothefuture-swagger:0.1.0 .
cd ..

# 모든 서비스 실행
docker compose down
docker compose up -d