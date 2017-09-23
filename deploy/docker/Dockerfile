FROM jenkins/jenkins:lts

USER root
RUN apt-get update
RUN apt-get install -y ruby
RUN apt-get install make
RUN /bin/bash -l -c "gem install eb_deployer"

# drop back normal jenkins user
USER jenkins