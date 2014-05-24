McRConLib
=========

Library for accessing a Minecraft server via RCon.
It is written in Scala, but should be fairly usable from Java.
It is based on [vincent's code](https://code.google.com/p/rcon-client/).

Example
-------

    val rcon = new RCon()

    // Parameters are host name, port and password
    rcon.connect("localhost", RCon.DEFAULT_PORT, "xxx")

    // Now we can send a command
    rcon.send("say Hello!")

    // And we can also use its return value
    val returnedSeedMessage = rcon.send("seed")
    println("As a reply to /seed we received: \"" + returnedSeedMessage + "\".")

    // When we're done we should close the connection.
    rcon.close()`

What can be seen on a server console:

    2014-05-24 21:09:43 [INFO] Rcon connection from: /127.0.0.1
    2014-05-24 21:09:43 [INFO] [Rcon] Hello!

Enabling RCon on Minecraft server
-----------------------
RCon have to be allow in `server.properties` like this:

    enable-rcon=true

If you want to password:

    rcon.password=xxx

And a custom port:

    rcon.port=25575
