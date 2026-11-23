DOCKER_ARCHIVE ?= build/docker/$(DOCKER_IMAGE_NAME)-$(DOCKER_IMAGE_TAG).tar
OAS_VALIDATOR_IMAGE ?= redocly/cli:1.34.5

.PHONY: help clean test build run oas-validate docker-save
help:
	@printf '%s\n' \
	  'make clean          Remove build output' \
	  'make test           Run the test suite' \
	  'make build          Build the executable Spring Boot jar' \
	  'make run            Run the service locally' \
	  'make oas-validate   Validate the OpenAPI contract' \
	  'make docker-build   Build and tag the image in the local Docker daemon' \
	  'make docker-save    Export the locally built image to a tar archive' \
	  'make docker-release Build and push the image to a registry'

clean:
	./gradlew clean

test:
	./gradlew test

build:
	./gradlew clean bootJar

run:
	./gradlew bootRun

oas-validate:
	docker run --rm --volume "$(CURDIR):/spec:ro" "$(OAS_VALIDATOR_IMAGE)" lint /spec/resources/oas/openapi.yaml

# BEGIN java-project-structure docker targets
DOCKER_REPOSITORY ?= localhost:5000
DOCKER_IMAGE_NAME ?= dummy-smtp-server
DOCKER_IMAGE_TAG ?= latest
DOCKER_IMAGE ?= $(DOCKER_REPOSITORY)/$(DOCKER_IMAGE_NAME):$(DOCKER_IMAGE_TAG)
DOCKERFILE ?= resources/docker/app/Dockerfile
DOCKER_CONTEXT ?= .

.PHONY: docker-build docker-release
docker-build:
	docker build --file "$(DOCKERFILE)" --tag "$(DOCKER_IMAGE)" "$(DOCKER_CONTEXT)"

docker-release: docker-build
	docker push "$(DOCKER_IMAGE)"
# END java-project-structure docker targets

docker-save: docker-build
	mkdir -p "$(dir $(DOCKER_ARCHIVE))"
	docker save --output "$(DOCKER_ARCHIVE)" "$(DOCKER_IMAGE)"
