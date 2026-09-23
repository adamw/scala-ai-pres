package pres

object S320_Orca_demo:
  // curl -fsSL https://raw.githubusercontent.com/VirtusLab/orca/master/install.sh | bash

  // simple.sc:
  import orca.{*, given}

  flow(OrcaArgs(Array.empty[String])):
    val openFindings = stage("Implement"):
      val session = codingAgent.session("implementer", seed = userPrompt)
      session.run("Implement the task from the seed prompt above.")
      reviewAndFixLoop(
        coderSession = session,
        reviewers = allReviewers(reviewAgent),
        task = Task(Title(userPrompt), ""),
        maxIterations = 3
      )

    openPrIfGitHub(
      summarisingAgent = codingAgent.cheap,
      openFindings = openFindings
    )
end S320_Orca_demo
