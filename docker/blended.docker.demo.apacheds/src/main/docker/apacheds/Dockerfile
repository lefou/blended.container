FROM atooni/blended-base:latest
MAINTAINER Blended Team version: 2.4.0-RC6-SNAPSHOT

RUN mkdir -p /opt/apacheds 

ENV JAVA_HOME /opt/java

ADD files /opt/apacheds

RUN yum install -y -q openldap-clients gettext vim

ADD apacheds-2.0.0-M24-x86_64.rpm /tmp

RUN yum install -y -q /tmp/apacheds-2.0.0-M24-x86_64.rpm

ENTRYPOINT ["/bin/bash", "-l", "/opt/apacheds/scripts/start.sh"]

EXPOSE 10389 

