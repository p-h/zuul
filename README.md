# zuul

Have you ever wanted to play a text adventure using only a simple TCP
client like [Telnet](https://en.wikipedia.org/wiki/Telnet) or
[Netcat](https://en.wikipedia.org/wiki/Netcat)?

Well now is your opportunity.

## Building

You'll need [Maven](https://maven.apache.org/) for this. Just a simple:

```
mvn package
```

And you are good to go.

## Running

Run the jar file.

```
java -jar target/zuul-1.0.jar

```

And now connect to the server. I'm using Netcat here but the same works
for Telnet

```
nc your.pcs.ip.address 7331
```

Type `help` ingame if you don't know what to do.

## Credits

This is based on Zuul from "Objects First with Java A Practical
Introduction using BlueJ" fifth Edition.
