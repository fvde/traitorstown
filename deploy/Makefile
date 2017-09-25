# OSX and Linux md5 command
UNAME := $(shell uname)
ifeq ($(UNAME),Linux)
MD5 = md5sum
endif
ifeq ($(UNAME),Darwin)
MD5 = md5 -q
endif

md5_war = $(shell $(MD5) $(WAR) | awk '{ print $$1 }')
md5_eb = $(shell $(MD5) .ebextensions/* | $(MD5)| awk '{ print $$1 }')
WAR_FILENAME = $(shell basename $(WAR))
WAR_DIRNAME = $(shell dirname $(WAR))
VERSION = $(md5_war)-$(md5_eb)-$(WAR_FILENAME)
VERSION_PATH = $(WAR_DIRNAME)/$(VERSION)

test:
	@echo $(VERSION)
	@echo $(WAR_DIRNAME)
	@echo cp $(WAR) $(VERSION_PATH)
	@echo $(shell uname)

shellinit:
	@cat ~/.aws-cli | grep "profile takeawayexpress" -A 2 | grep aws | awk -F '=' '{ printf("export %s=%s\n", toupper($$1), $$2) }'

install:
	gem install eb_deployer

clean:
	rm -f *.war

ROOT.war:
	@echo Deploying $(VERSION) ...
	cp $(WAR) $(VERSION_PATH)
	zip -r $(VERSION_PATH) .ebextensions/*

# Traitor's Town deployment

deploy_traitors: ROOT.war
	#echo $(VERSION) $(VERSION_PATH)
	VERSION=$(VERSION) eb_deploy -p $(VERSION_PATH) -e traitors-prod -c config/traitors.yml

learn_traitors: ROOT.war
	#echo $(VERSION) $(VERSION_PATH)
	VERSION=$(VERSION) eb_deploy -p $(VERSION_PATH) -e traitors-learn -c config/traitors.yml

destroy_learn_traitors:
	@echo Shutting down...
	eb_deployer -d -e traitors-learn