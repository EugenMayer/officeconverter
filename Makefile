ifdef RUNTIME_VERSION
    $(info RUNTIME_VERSION defined manually)
else
    RUNTIME_VERSION := 0.1.6
endif

build: stop
	docker build --pull --build-arg RUNTIME_VERSION=$(RUNTIME_VERSION) --build-arg VERSION=0.0.1-SNAPSHOT --target development . -t ghcr.io/eugenmayer/kontextwork-converter:development
	docker build --pull --build-arg RUNTIME_VERSION=$(RUNTIME_VERSION) --build-arg VERSION=0.0.1-SNAPSHOT --target production . -t ghcr.io/eugenmayer/kontextwork-converter:production

start-src: stop
	./start.sh

watch:
	./watch.sh

start: stop
    # 5001 is the remote debugger port, which we enable by default
	docker run -m 2048m --name converter-dev --rm -p 5001:5001 -p 8080:8080 ghcr.io/eugenmayer/kontextwork-converter:development

start-prod: stop
	docker run -m 2048m --name converter-prod --rm -p 8080:8080 ghcr.io/eugenmayer/kontextwork-converter:production

test:
	docker run -m 2048m --name converter-test --rm -v ${PWD}/.:/src --workdir /src ghcr.io/eugenmayer/kontextwork-converter:development ./gradlew itTest

stop:
	docker stop --name converter-prod > /dev/null 2>&1 || true
	docker stop --name converter-dev > /dev/null 2>&1 || true

build-local:
	./gradlew build

start-local:
	./gradlew bootRun

test-local:
	./gradlew test
