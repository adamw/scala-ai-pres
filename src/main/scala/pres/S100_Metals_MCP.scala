package pres

object S100_Metals_MCP:
  // Default: sbt invoked via shell; javap; grep

  val _1 = "A set of tools to interface with the project's build"
  val tools = List(
    "import-build",
    "compile-full",
    "compile-module",
    "glob-search",
    "typed-glob-search",
    "find-dep"
  )

  // claude mcp add --transport http metals "http://localhost:33131/mcp"
  // claude mcp remove metals
