FROM jenkins/jenkins:lts

USER root
RUN apt-get update
RUN apt-get install -y ruby
RUN apt-get install make
RUN /bin/bash -l -c "gem install eb_deployer"

# Environment Variables
ENV AWS_ACCESS_KEY_ID AKIAIAU6VICX5SDUBCUA
ENV AWS_SECRET_ACCESS_KEY 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

# drop back normal jenkins user
USER jenkins