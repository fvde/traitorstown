FROM jenkins/jenkins:lts

USER root
RUN apt-get update && apt-get install -y ruby
RUN /bin/bash -l -c "gem install eb_deployer"

# drop back normal jenkins user
USER jenkins