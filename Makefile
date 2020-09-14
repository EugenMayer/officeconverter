release: build tag push

build: stop
	docker pull eugenmayer/jodconverter:base
	source ./VERSION && docker build --build-arg VERSION=$${VERSION} --target development . -t eugenmayer/kontextwork-converter:development
	source ./VERSION && docker build  --build-arg VERSION=$${VERSION} --target production . -t eugenmayer/kontextwork-converter:production

push: tag
	docker push eugenmayer/kontextwork-converter:development
	docker push eugenmayer/kontextwork-converter:production
	source ./VERSION && docker push eugenmayer/kontextwork-converter:"$${VERSION}"
	git push
	git push --tags

tag:
	source ./VERSION && git tag "v$${VERSION}"
	source ./VERSION && docker tag eugenmayer/kontextwork-converter:production eugenmayer/kontextwork-converter:$${VERSION}

start-src: stop
	./start.sh

watch:
	./watch.sh

start: stop
    # 5001 is the remote debugger port, which we enable by default
	docker run -m 512m --name converter-dev --rm -p 5001:5001 -p 8080:8080 eugenmayer/kontextwork-converter:development

start-prod: stop
	docker run -m 512m --name converter-prod --rm -p 8080:8080 eugenmayer/kontextwork-converter:production

test:
	docker run -m 512m --name converter-test --rm -v ${PWD}/.:/src --workdir /src eugenmayer/kontextwork-converter:development ./gradlew itTest

stop:
	docker stop --name converter-prod > /dev/null 2>&1 || true
	docker stop --name converter-dev > /dev/null 2>&1 || true

build-local:
	SPRING_PROFILES_ACTIVE=dev ./gradlew build

start-local:
	SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun

test-local:
	./gradlew test
