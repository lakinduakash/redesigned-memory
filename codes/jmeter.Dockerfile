FROM openjdk:8
COPY apache-jmeter-5.3 /usr/share/apache-jmeter-5.3
COPY jtl-splitter-0.4.5.jar /usr/share/jtl-splitter-0.4.5.jar
WORKDIR /usr/tests/results
CMD ["/usr/tests/jmx/base-3-part-2.sh"]
