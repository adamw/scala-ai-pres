package pres

object S100_Metals_MCP:
  // Default: sbt invoked via shell; javap; grep

  val _1 = "A set of tools to interface with the project's build"
  val tools = List(
    "import-build",
    "compile-full",
    "compile-module",
    "globa-search",
    "typed-glob-search",
    "find-dep"
  )

  // Alternative: sbt --client
