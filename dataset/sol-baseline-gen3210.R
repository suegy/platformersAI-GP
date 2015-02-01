library(splitstackshape)

sol.baseline.gen3210$V101 <- NULL

for (i in seq(1:100)){
  on <- paste0("V",i)
  nn <- paste0("Agent_",i)
  colnames(sol.baseline.gen3210) [colnames(sol.baseline.gen3210) == on] <- c(nn)
  sol.baseline.gen3210 <- cSplit(sol.baseline.gen3210, c(nn), c(" "))
  for (j in seq(1:9)) {
    cn <- paste0(nn,"_lvl",j-1)
    colnames(sol.baseline.gen3210) [colnames(sol.baseline.gen3210) == paste0(nn,"_0",j) ] <- c(cn)
    sol.baseline.gen3210 <- cSplit(sol.baseline.gen3210, c(cn), c(":"))
  }
  colnames(sol.baseline.gen3210) [colnames(sol.baseline.gen3210) == paste0(nn,"_",10) ] <- c(paste0(nn,"_lvl",9))
  sol.baseline.gen3210 <- cSplit(sol.baseline.gen3210, c(paste0(nn,"_lvl",9), c(":"))
}

baseline.gen3210 <- data.frame()
for (a in seq(1:100)){
  un <- paste("Agent",a,"lvl0",0,sep = "_")
  nn <- paste("Agent",a,"fitness",sep = "_")
  colnames(sol.baseline.gen3210) [colnames(sol.baseline.gen3210) == un ] <- c(nn)
}


for (gen in seq(1:nrow(sol.baseline.gen3210)){
  for (a in seq(1:100)){
    for (lvl in seq(0:9)){
      un <- paste("Agent",a,paste0("lvl",lvl),sep = "_")
      row <- c(sol.baseline.gen3210)
      baseline.gen3210 <- rbind(baseline.gen3210,row)
    }
  }
}

for (i in seq(1:100)) {
  for (j in seq(0:9)){
    colnames(sol.baseline.gen3210) [colnames(sol.baseline.gen3210) == paste0("Agent_",i,"_lvl",j,"_2") ] <- c(paste0("Agent_",i,"_lvl",j,"_distance"))
    colnames(sol.baseline.gen3210) [colnames(sol.baseline.gen3210) == paste0("Agent_",i,"_lvl",j,"_1") ] <- c(paste0("Agent_",i,"_lvl",j,"_fitness"))
  }
}
baseline.gen3209 <- data.frame()

for (gen in seq(nrow(sol.baseline.gen3210))){
  for (a in seq(1:100)){
    for (lvl in seq(0:9)){
      fit <- sol.baseline.gen3210[[paste0("Agent_",a,"_lvl",lvl,"_fitness")]][gen]
      dist <- sol.baseline.gen3210[[paste0("Agent_",a,"_lvl",lvl,"_distance")]][gen]
      
      baseline.gen3209 <- rbind(baseline.gen3209,c(fitness=fit,distance=dist,generation=gen,level=lvl))
    }
  }
}