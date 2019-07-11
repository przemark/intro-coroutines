package tasks

import contributors.*

suspend fun loadContributorsProgress(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .body() ?: listOf()

//    val users = mutableListOf<User>()
//
//    repos.forEach { repo ->
//        val contributors = service
//            .getRepoContributors(req.org, repo.name)
//            .also { logUsers(repo, it) }
//            .bodyList()
//
//        users += contributors
//        updateResults(users.aggregate(), false)
//
//    }
//
//    updateResults(users.aggregate(), true)

    var allUsers = emptyList<User>()
    for ((index, repo) in repos.withIndex()) {
        val users = service
            .getRepoContributors(req.org, repo.name)
            .also { logUsers(repo, it) }
            .bodyList()

        allUsers = (allUsers + users).aggregate()
        updateResults(allUsers, index == repos.lastIndex)
    }
}
