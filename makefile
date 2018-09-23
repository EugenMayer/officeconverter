build:
	docker build --target development . -t eugenmayer/converter:development
	docker build --target production . -t eugenmayer/converter:production


push:
	docker push eugenmayer/converter:development
	docker push eugenmayer/converter:production

start: stop
	docker run --name converter-dev --rm -p 8080:8080 eugenmayer/converter:development

start-prod: stop
	docker run --name converter-prod --rm -p 8080:8080 eugenmayer/converter:production

stop:
	docker stop --name converter-prod > /dev/null 2>&1 || true
	docker stop --name converter-dev > /dev/null 2>&1 || true

build-local:
	SPRING_PROFILES_ACTIVE=dev ./gradlew build

start-local:
	SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun