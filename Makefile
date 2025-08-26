DOCKER     ?= docker
APP_IMAGE  ?= tasks-api
APP_NAME   ?= tasks-api
PORT       ?= 8080

MVN_IMAGE  ?= maven:3.9-eclipse-temurin-21
WORKDIR    ?= /w
M2_VOL     ?= m2-cache
MVN_DOCKER := $(DOCKER) run --rm -v $(PWD):$(WORKDIR) -w $(WORKDIR) -v $(M2_VOL):/root/.m2 $(MVN_IMAGE)

.DEFAULT_GOAL := help
.PHONY: help docker-check image start stop logs test lint clean

help: ## Show available targets and variables
	@echo ""
	@echo "Targets:"
	@grep -E '^[a-zA-Z0-9_-]+:.*## ' $(MAKEFILE_LIST) | awk 'BEGIN{FS=":.*## "}{printf "  \033[36m%-12s\033[0m %s\n",$$1,$$2}'
	@echo ""
	@echo "Vars: DOCKER=$(DOCKER)  APP_IMAGE=$(APP_IMAGE)  APP_NAME=$(APP_NAME)  PORT=$(PORT)"

docker-check:
	@command -v $(DOCKER) >/dev/null 2>&1 || { echo "Docker CLI not found: $(DOCKER)"; exit 1; }

image: docker-check ## Build the Docker image (compiles the app inside Docker)
	@$(DOCKER) build -t $(APP_IMAGE) .

start: image ## Run the container in background (host $(PORT) -> container 8080)
	@$(DOCKER) rm -f $(APP_NAME) >/dev/null 2>&1 || true
	@$(DOCKER) run -d --name $(APP_NAME) -p $(PORT):8080 $(APP_IMAGE)
	@echo ">> Running at http://localhost:$(PORT)"

stop: docker-check ## Stop and remove the container
	@$(DOCKER) rm -f $(APP_NAME) >/dev/null 2>&1 || true
	@echo ">> Stopped"

logs: docker-check ## Tail container logs
	@$(DOCKER) logs -f $(APP_NAME)

test: docker-check ## Run Maven tests
	@$(DOCKER) volume create $(M2_VOL) >/dev/null
	@$(MVN_DOCKER) mvn test -Dsurefire.printSummary=true

lint: docker-check ## Run style checks
	@$(DOCKER) volume create $(M2_VOL) >/dev/null
	@$(MVN_DOCKER) mvn -DskipTests spotless:check || true
	@$(MVN_DOCKER) mvn -DskipTests -Dcheckstyle.failOnViolation=false checkstyle:check

lint-fix: docker-check ## Apply lint formatting
	@$(DOCKER) volume create $(M2_VOL) >/dev/null
	@$(MVN_DOCKER) mvn -DskipTests spotless:apply
	@echo ">> Re-run 'make lint' to see remaining Checkstyle warnings."

clean: docker-check ## Clean build artifacts and remove the container (if any)
	@$(DOCKER) volume create $(M2_VOL) >/dev/null
	@$(MVN_DOCKER) mvn clean || true
	@$(DOCKER) rm -f $(APP_NAME) >/dev/null 2>&1 || true
	@echo ">> Cleaned"
