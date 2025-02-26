FROM maven:3.9.9-eclipse-temurin-17

WORKDIR /app

COPY . /app

CMD ["bash", "-c", "rm -r target/*; mvn -DremoteSeleniumUrl=$SELENIUM_URL test"]