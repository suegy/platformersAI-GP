
library(splitstackshape)

gamalyzer.cpf12.sw10.gen7410$V101 <- NULL

for (i in seq(1:100)){
  on <- paste0("V",i)
  nn <- paste0("Agent.",i)
  colnames(gamalyzer.cpf12.sw10.gen7410) [colnames(gamalyzer.cpf12.sw10.gen7410) == on] <- c(nn)
  gamalyzer.cpf12.sw10.gen7410 <- concat.split.multiple(gamalyzer.cpf12.sw10.gen7410, c(nn), c(":"))
  colnames(gamalyzer.cpf12.sw10.gen7410) [colnames(gamalyzer.cpf12.sw10.gen7410) == paste0(nn,"_1") ] <- c(paste0(nn,".tfit"))
  colnames(gamalyzer.cpf12.sw10.gen7410) [colnames(gamalyzer.cpf12.sw10.gen7410) == paste0(nn,"_2") ] <- c(paste0(nn,".fit"))
  colnames(gamalyzer.cpf12.sw10.gen7410) [colnames(gamalyzer.cpf12.sw10.gen7410) == paste0(nn,"_3") ] <- c(paste0(nn,".dist"))
}
gamalyzer.cpf12.sw10.gen7410 <- do.call(data.frame,lapply(gamalyzer.cpf12.sw10.gen7410, function(x) replace(x, is.infinite(x),0.0)))

gamalyzer.cpf12.sw10.gen7410$Agents.fit <- rowSums(gamalyzer.cpf12.sw10.gen7410[grepl(pattern = "\\.fit",colnames(gamalyzer.cpf12.sw10.gen7410),perl = TRUE)])
gamalyzer.cpf12.sw10.gen7410$Agents.tfit <- rowSums(gamalyzer.cpf12.sw10.gen7410[grepl(pattern = "\\.tfit",colnames(gamalyzer.cpf12.sw10.gen7410),perl = TRUE)])
gamalyzer.cpf12.sw10.gen7410$Agents.avgdist <- rowMeans(gamalyzer.cpf12.sw10.gen7410[grepl(pattern = "\\.dist",colnames(gamalyzer.cpf12.sw10.gen7410),perl = TRUE)])
gamalyzer.cpf12.sw10.gen7410$Agents.avgfit <- rowMeans(gamalyzer.cpf12.sw10.gen7410[grepl(pattern = "\\.fit",colnames(gamalyzer.cpf12.sw10.gen7410),perl = TRUE)])


maxFit <- max.col(gamalyzer.cpf12.sw10.gen7410[grepl(pattern = "[0-9]\\.fit",colnames(gamalyzer.cpf12.sw10.gen7410),perl = TRUE)])
maxDist <- max.col(gamalyzer.cpf12.sw10.gen7410[grepl(pattern = "[0-9]\\.dist",colnames(gamalyzer.cpf12.sw10.gen7410),perl = TRUE)])

simDistData <- data.frame(fit = gamalyzer.cpf12.sw10.gen7410[grepl(pattern = "\\.fit",colnames(gamalyzer.cpf12.sw10.gen7410),perl = TRUE)][1,maxFit[1]],dist = gamalyzer.cpf12.sw10.gen7410[grepl(pattern = "\\.dist",colnames(gamalyzer.cpf12.sw10.gen7410),perl = TRUE)][1,maxDist[1]])
for (i in seq(2:length(maxDist))){
  simDistData <- rbind(simDistData, c(fit = gamalyzer.cpf12.sw10.gen7410[grepl(pattern = "\\.fit",colnames(gamalyzer.cpf12.sw10.gen7410),perl = TRUE)][i,maxFit[i]],dist = gamalyzer.cpf12.sw10.gen7410[grepl(pattern = "\\.dist",colnames(gamalyzer.cpf12.sw10.gen7410),perl = TRUE)][i,maxDist[i]]))
}

sum(is.infinite(gendist))
gendist[is.infinite(gendist)] <- 0.0

gendist <- matrix(data=0.0, nrow=7408, ncol=256, byrow=FALSE, dimnames = list(c(seq(1:7408)),c(seq(1:256))))
for (i in seq(1:length(simDistData$dist))){
  gendist[i,simDistData$dist[i]] <- simDistData$fit[i]
}


time <- data.frame(gamalyzer.cpf12.sw10.gen7410[grepl(pattern = "\\.fit",colnames(gamalyzer.cpf12.sw10.gen7410),perl = TRUE)],gamalyzer.cpf12.sw10.gen7410[grepl(pattern = "\\.dist",colnames(gamalyzer.cpf12.sw10.gen7410),perl = TRUE)])
smoothScatter(gamalyzer.cpf12.sw10.gen7410$Agents.avgdist,gamalyzer.cpf12.sw10.gen7410$Agents.avgfit)