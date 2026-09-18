//> using scala 3.8.4
//> using dep "org.virtuslab::orca:0.1.7"
//> using jvm 21

import orca.{*, given}

flow(OrcaArgs(args)):
  val plan = stage("Plan"):
    Plan.autonomous.from(userPrompt, planningAgent).value

  val session = codingAgent.session("implementer", seed = plan.brief)

  val taskDeclines =
    for task <- plan.tasks yield stage(s"Task: ${task.title}"):
      session.run(task.description)
      reviewThenFix(
        coderSession = session,
        reviewers = allReviewers(reviewAgent),
        task = task
      )

  val openFindings = stage("Final review"):
    reviewAndFixLoop(
      coderSession = session,
      reviewers = allReviewers(reviewAgent),
      task = Task(Title("The whole planned change"), plan.brief),
      diff = ReviewDiff.WholeRun,
      maxIterations = 5,
      priorDeclines = IgnoredIssues(taskDeclines.flatMap(_.issues))
    )

  openPrIfGitHub(
    summarisingAgent = codingAgent.cheap,
    openFindings = openFindings
  )
