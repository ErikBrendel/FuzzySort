#Install
install.packages("irace")
#Setup
library("irace")


scenario <- readScenario(filename = "scenario.txt", scenario = defaultScenario())
irace.main(scenario = scenario)


# -- Util
plotFile <- function (fn, name) {
  png(file = paste(name, "png", sep = "."), width=1024, height=1024, pointsize=24)
  try(fn())
  dev.off()
}

plotAll <- function(pfn, arr, prefix="plot") {
  for (elem in arr) {
    plotFile(function() {
      pfn(elem)
    }, paste(prefix, elem, sep = "-"))
  }
}

# -- Analysis
load("irace.Rdata")

numElites <- length(iraceResults$iterationElites)
iterationCount <- nrow(iraceResults$experiments)
conf<-getConfigurationByIteration(iraceResults=iraceResults, iterations=numElites-2)

elites <- getConfigurationById(iraceResults, ids = unique(unlist(iraceResults$allElites[-1])))

parameterFrequency(iraceResults$allConfigurations, iraceResults$parameters)
parameterFrequency(elites, iraceResults$parameters)

parallelCoordinatesPlot(elites, iraceResults$parameters,hierarchy=FALSE)
parallelCoordinatesPlot(iraceResults$allConfigurations, iraceResults$parameters, hierarchy = FALSE)

plotAll(function(elem) {
  conf<-getConfigurationByIteration(iraceResults=iraceResults, iterations=elem)
  parallelCoordinatesPlot(conf, iraceResults$parameters, hierarchy=FALSE)
}, 1:numElites) # WARNING: parallelCoordidates expects at least 2 configurations
