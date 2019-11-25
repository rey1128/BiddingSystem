FROM openjdk:8-jdk

RUN mkdir /home/bidding
COPY ./build/libs/BasicBidding-all.jar /home/bidding

CMD ["java","-jar", "/home/bidding/BasicBidding-all.jar"]