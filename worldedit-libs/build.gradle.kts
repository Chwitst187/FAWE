tasks.register("build") {
    dependsOn(subprojects.mapNotNull {
        it.tasks.findByName("jar") ?: it.tasks.findByName("build")
    })
}
