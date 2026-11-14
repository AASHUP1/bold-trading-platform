pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BOLD-Trading"

include(":app")
include(":core:common")
include(":core:designsystem")
include(":core:database")
include(":core:networking")
include(":core:charts")
include(":feature:onboarding")
include(":feature:dashboard")
include(":feature:watchlist")
include(":feature:charts")
include(":feature:papertrading")
include(":feature:options")
include(":feature:scanner")
include(":feature:algo")
