# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)
https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAFZM9qBACu2AMQALADMABwATACcIDD+yPYAFmA6CD6GAEoo9kiqFnJIEGiYiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAEQDlGjAALYo43XjMOMANCu46gDu0ByLy2srKLPASAj7KwC+mMK1MJWs7FyUDRNTUDPzF4fjm6o7UD2SxW63Gx1O52B42ubE43FgD1uogaUCyOTAlAAFJlsrlKJkAI5pXIAShuNVE9yqsnkShU6ga9hQYAAqoNMe9PigyTTFMo1KoqUYdHUAGJITgwNmUXkwHSWGCcuZiHSo4AAaylgxgWyQYASisGXJgwAQao4CpQAA90RpeXSBfdERSVA1pVBeeSRConVVbi8YAozShgBaOhr0ABRK0qbAEQpeu5lB4lcwNQJOYIjCbzdTAJmLFaRqDeeqG6bKk3B0MK+Tq9DQsycTD2-nqX3Vb0oBpoHwIBCJykPVv01R1EBqjHujmDXk87QO9sPYx1BQcDhamXaQc+4cLttjichjEKHz6zHAM8JOct-ejoUrtcb0-6z1I3cPWHPMs49H4tR9lgX7wh2-plm8RrKssDQHKCl76h0ED1mg0ErFciaUB2qYYA04ROE42aTJBXwwDBIIrPBCSIchqEHNc6AcKYXi+AE0DsEysSinAkbSHACgwAAMhA2RFNhzDOtQAYtO03R9AY6gFGghFKl8UKgn8AJAnRGEIlUwEBhBFaqbBGz6P8uzfDCTwgRJVDIjACDCRKmJCSJhLEmAZLvoYe60gejLMtOKncrefn3suIowOKkrurK8rlh8yqYKqIaau6xocBAagwGgEDMNa6KhXy4W2fZvb9juPm2QG0hZaoAByeXRui0axvGRRgVhyBpjAGYEWM4y5qo+YLDBxalg0egbqiRJqGAjYMZVQreQ0VCmkgWgYs0ZmaZiZIpRqMASmtyAbtt2y7EtyZ+jUAYAJJoCdG3neZgKtSgcaKTpXWlGA6ZOAAjIRQ0jYW4zjdADQ+DMV7QEgABeKB7PRzYjo612dnZrqbh627eUKaMMjAR5yCgL4JBeV43oTgoRQ0T6BlT2406BLrdozCFIegV2ftZAauRKmSqIBmD6Qi1XgURRkLKRaE-JR1ENrL2mdRjYm4fhynEaNsHjArXMoVCKOMZ43h+P4XgoOgsTxEklvW65vhYGJy23WWjTSJGAmRh0kY9L08mqIpIz68h3283CAah9zYuu12DSOfYTsucJTvuXNXlswTd4CgFYDk5i5OK2g85hejVQrlFErPkz8jJWqmoPU9Nec2HLMYytOV9gO+MY2LDRN+tHDk+9n0JrHHdu-9QMDSDBZjSWkOKjD+pw4jyPhym3U4b1gPAwKoMLxNy+UWvSMLajOdLqV2Pk2+We+cVudGCg3Anlehe18ApdP0uFeRdIV+zJDB3zxg-CO34GiOzPELEWE8JavGuKrLev0Nb9VGMbJiZsAiog3P4bAEpNQCXRDAAA4sqDQLsEFNFIT7f29hlQhyvMXcOek+ZlmjkUCeN144OXRC5dE6dSRXR4VjdmzdSZFwNpTVu6B9oNxgIPU6HMqIGx5mwyOZYlEbRHjGD67VRbsNZpJMsGYZ6jBWHPUaRZF5lmhqfKACNz4wCQW7H6PUzHZksQfeeNjj72Nho49eyxMG92pFfMcfDcjkNzHtIqi5ab-waNFDcMTsrENyPEg8xixENDSRkrAvcIHwjqAU2BCAgJGMniY14KwGG5kLI0CY9SUB3WkIWAG4RgiBFBFsBIeoUDpSgmpFYKRQDqiGcZci4wWn1WGXRGAXRXEmPcTvPC6C6kUMac05UbSOldJ6SsPpAzJk62mWMkAEztbfFBLM+ZVxFmYNNixfwHAADskQnAoCcLESMwQ4DcQAGzwEnIYNJMBijb3EqIqSbROj0MYSvVRyFCJ3OMo2VWGjIEmmYQbVFyo5noqspouOuTiagsxHAUFQjPIiMxvZCR79ZFoBkciuR9dUqKMekPFRLCilYpKVy5uui2pfXgTC0xe8xjeLzL48GtioZIrPnsFxm8qjq13uYmVw05UQzsUqoJzjMEHUbty5RaLDDQDlKGGAM0PJyggBadRjxNENAAEKhhpaPAx4rMYBj6l4waPjrHyuPlNW1KBZq5BCWq+AUL-obKDbKkNerJo2rtXNGNi0wkyAiQ0EmGI0mYgtT-BJD5IoMzSVkkqoj7JVv5S67FVLjwoHKZUkl1Tai1Jmbs9pDROndNVcg9V8aYDrMDS0vZ-aDmqsWs882lhX6OS2DbJAiQwCLv7BAFdAApCAEoyGVn8Bc9UkLfqkthSyWSvQWlMOZdmbACBgCLqgHACAjkoDrEndIZZSYBVR1xSisYj7n2UDfR+r9vbiXflJfZAAVvulle7BaRo8pnLssHsaMrJoB9ArLi7yM5dolubKOrgP-Vos1OirzerFVU6hnjpVJp1SmhVJ9AlOJVb+zCatR2MYscxw+fil4BNXoarjTZGImqFTy79MArXhozbkB1TqG39xgB6jgXq9Fjy4fRiVCbA1WLBqm6101UOZqHW43jqDd6JuM0fJeimLPRtnc2HNNM85Fu-aW7JdMq6Sire3G+7Mq3SfBaac0tYIxkYw33dh7rPUubALR8e+m-WSqzExhzwmOHVgtDAOsStuPixHbZjMWWBM5dDUvSLIYCtFZQm5xiYTPOKmwJtFARa0m+fCkkqUHXSaHvmNW9GIW8lJTUwlmAyG0BtsMR26h46xgldWX9MdmsVuSawS8rwz7V3rr2wqRAIZYDAGwI+wg+RCgQqoQZpontva+39sYVhjbBWYlyipywZJuH0uxiAbgeBMSA9OzS9DYjs5lyJqD4HvXy7CgaIAt+ID+y5vkDAAAZt4WY6PdD6DEMF2t2NhYHmdepk7eB5u+rAmg7Mq2bM9WWxg7bppmAPQxIlBAGWYB6tjRqtwm2qvBpM2xiUnOZjc8GHcSgpYs3NiAA


## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
