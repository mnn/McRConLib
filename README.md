McRConLib
=========

Library for accessing a Minecraft server via RCon.
It is written in Scala, but should be fairly usable from Java.
It is based on [vincent's code](https://code.google.com/p/rcon-client/).

Example
-------

```scala
val rcon = new RCon()

// Parameters are host name, port and password
rcon.connect("localhost", RCon.DEFAULT_PORT, "xxx")

// Now we can send a command
rcon.send("say Hello!")

// And we can also use its return value
val returnedSeedMessage = rcon.send("seed")
println("As a reply to /seed we received: \"" + returnedSeedMessage + "\".")

// When we're done we should close the connection.
rcon.close()
```

What can be seen on a server console:

    2014-05-24 21:09:43 [INFO] Rcon connection from: /127.0.0.1
    2014-05-24 21:09:43 [INFO] [Rcon] Hello!

Enabling RCon on Minecraft server
---------------------------------

RCon have to be allowed in `server.properties` like this:

    enable-rcon=true

If you want to use a password:

    rcon.password=xxx

And a custom port:

    rcon.port=25575

Donation
--------
If you like this library, please consider a small donation - [my PayPal](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=U6PGB7P24WWSU&lc=CZ&item_name=monnef%20%2d%20McRConLib&item_number=10&currency_code=CZK&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted).

License
-------
McRConLib: Library for accessing Minecraft server via RCon

Copyright (C) 2014  monnef

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see [<http://www.gnu.org/licenses/>](http://www.gnu.org/licenses/).