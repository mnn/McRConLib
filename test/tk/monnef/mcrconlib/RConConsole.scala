package tk.monnef.mcrconlib

/**
 * Simple console application for testing (not intended for end-user).
 * @author monnef
 */
object RConConsole extends App {
  val rcon = new RCon
  args.length match {
    case 2 => rcon.connect(args(0), RCon.DEFAULT_PORT, args(1))
    case 3 => rcon.connect(args(0), args(1).toInt, args(2))
    case _ =>
      println("Expected two/three command line arguments - <host> [port] <password>")
      sys.exit(-1)
  }
  var input = ""
  var working = true
  println("For exit type \"exit\".")
  do {
    input = readLine()
    if (input == "exit") working = false
    else println(rcon.send(input))
  } while (working)
}
