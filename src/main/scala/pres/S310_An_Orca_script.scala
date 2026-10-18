package pres

class S310_An_Orca_script:
  // > using scala 3.9.0
  // > using dep "org.virtuslab::orca:0.2.0"
  // > using jvm 21

  import orca.{*, given}

  flow(OrcaArgs(Array.empty[String])):
    val plan = stage("Plan"):
      Plan.autonomous.from(userPrompt, planningAgent).value

    val taskOpenFindings =
      for task <- plan.tasks yield stage(s"Task: ${task.title}"):
        val session = codingAgent.session("implementer", seed = plan.brief)
        session.run(task.description)
        reviewThenFix(
          coderSession = session,
          reviewers = allReviewers(reviewAgent),
          task = task
        )

    // Nothing reviews after this loop, so it gets more fix turns.
    val openFindings = stage("Final review"):
      val finalFixer = codingAgent.session("final-fixer", seed = plan.brief)
      reviewAndFixLoop(
        coderSession = finalFixer,
        reviewers = allReviewers(reviewAgent),
        task = Task(Title("The whole planned change"), plan.brief),
        diff = ReviewDiff.WholeRun,
        maxFixTurns = 5,
        priorOpenFindings = taskOpenFindings.flatMap(_.findings)
      )

    openPrIfGitHub(
      summarisingAgent = codingAgent,
      openFindings = openFindings
    )
