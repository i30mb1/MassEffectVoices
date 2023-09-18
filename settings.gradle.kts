rootProject.name = "mev"

pluginManagement {
        includeBuild("build-logic/build-settings")
}

plugins {
        id("n7.plugins.settings")
}

include(
        ":app",
        ":feature:avina",
        ":feature:edi",
        ":feature:garrus",
        ":feature:grunt",
        ":feature:illusive_man",
        ":feature:jack",
        ":feature:jacob",
        ":feature:javik",
        ":feature:joker",
        ":feature:kasumi",
        ":feature:legion",
        ":feature:liara",
        ":feature:miranda",
        ":feature:mordin",
        ":feature:reaper",
        ":feature:samara",
        ":feature:tali",
        ":feature:thane",
        ":feature:vega",
        ":feature:zaeed",
        ":feature:wrex",
        ":feature:anderson",
        ":feature:kaidan",
        ":feature:ashley",
        ":feature:rachni_queen",
)
