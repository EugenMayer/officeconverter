
build: stop
	docker build --target development . -t eugenmayer/kontextwork-converter:development
	docker build --target production . -t eugenmayer/kontextwork-converter:production

push:
	docker push eugenmayer/kontextwork-converter:development
	docker push eugenmayer/kontextwork-converter:production

start: stop
    # 5001 is the remote debugger port, which we enable by default
	docker run -m 512m --name converter-dev --rm -p 5001:5001 -p 8080:8080 eugenmayer/kontextwork-converter:development

start-prod: stop
	docker run -m 512m --name converter-prod --rm -p 8080:8080 eugenmayer/kontextwork-converter:production

stop:
	docker stop --name converter-prod > /dev/null 2>&1 || true
	docker stop --name converter-dev > /dev/null 2>&1 || true

build-local:
	SPRING_PROFILES_ACTIVE=dev ./gradlew build

start-local:
	SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun