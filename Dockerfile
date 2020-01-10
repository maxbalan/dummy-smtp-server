FROM openjdk:13.0.1-oraclelinux7

MAINTAINER com.github.maxbalan

#adding user
RUN useradd -ms /bin/bash dummysmtp

#copy service image
ADD build/distributions/dummy-smtp-server.tar /home/dummysmtp/
ADD docker/entrypoint.sh ./entrypoint.sh

RUN chmod +x /entrypoint.sh
RUN chown -R dummysmtp:dummysmtp /home/dummysmtp

# expose ENV variables for the service
ENV HOME /home/dummysmtp
ENV DUMMY_SMTP_SERVICE_PORT 9086
ENV DUMMY_SMTP_SERVICE_ADMIN_PORT 9087

ENV DUMMY_SMTP_SERVER_PORT 6969

#Expose ports
EXPOSE 6969:6969
EXPOSE 9086:9086

# Slim down the image
RUN rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

ENTRYPOINT ["/bin/bash"]
CMD ["./entrypoint.sh"]
