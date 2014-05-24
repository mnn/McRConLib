package tk.monnef.mcrconlib.basic

import tk.monnef.mcrconlib.RCon

/**
 * @author monnef
 */
object Main extends App {
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
}
