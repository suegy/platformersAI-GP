library(splitstackshape)

sol.baseline.gen3210$V101 <- NULL

for (i in seq(1:100)){
  on <- paste0("V",i)
  nn <- paste0("Agent_",i)
  setnames(sol.baseline.gen3210, old = on, new = nn )
  sol.baseline.gen3210 <- cSplit(sol.baseline.gen3210, c(nn), c(" "))
  for (j in seq(1:9)) {
    cn <- paste0(nn,"_lvl",j-1)
    setnames(sol.baseline.gen3210, old= paste0(nn,"_0",j), new = cn)
    
    sol.baseline.gen3210 <- cSplit(sol.baseline.gen3210, c(cn), c(":"))
  }
  setnames(sol.baseline.gen3210, old = paste0(nn,"_",10), new = c(paste0(nn,"_lvl",9)))
  sol.baseline.gen3210 <- cSplit(sol.baseline.gen3210, c(paste0(nn,"_lvl",9)), c(":"))
}

baseline.gen3210 <- data.frame()
for (a in seq(1:100)){
  un <- paste("Agent",a,"lvl0",1,sep = "_")
  nn <- paste("Agent",a,"fitness",sep = "_")
  setnames(sol.baseline.gen3210, old = un, new = nn)
  setnames(sol.baseline.gen3210, old = paste("Agent",a,"lvl0",2,sep = "_"), new = paste("Agent",a,"lvl0",1,sep = "_"))
  setnames(sol.baseline.gen3210, old = paste("Agent",a,"lvl0",3,sep = "_"), new = paste("Agent",a,"lvl0",2,sep = "_"))
}

for (i in seq(2,100,1)) {
  for (j in seq(1,9,1)){
    setnames(sol.baseline.gen3210, old = paste0("Agent_",i,"_lvl",j,"_2"), new =  c(paste0("Agent_",i,"_lvl",j,"_distance")) )
    setnames(sol.baseline.gen3210, old = paste0("Agent_",i,"_lvl",j,"_1"), new = c(paste0("Agent_",i,"_lvl",j,"_fitness")))
  }
}
for (i in seq(1:100)) {
  setnames(sol.baseline.gen3210, old = paste0("Agent_",i,"_lvl0_2"), new =  c(paste0("Agent_",i,"_lvl0_distance")) )
  setnames(sol.baseline.gen3210, old = paste0("Agent_",i,"_lvl0_1"), new = c(paste0("Agent_",i,"_lvl0_fitness")))
}
baseline.gen3209 <- data.frame()

for (gen in seq(nrow(sol.baseline.gen3210))){
  print(gen)
  for (a in seq(1:100)){
    for (lvl in seq(1:10)){
      fit <- sol.baseline.gen3210[[paste0("Agent_",a,"_lvl",lvl-1,"_fitness")]][gen]
      dist <- sol.baseline.gen3210[[paste0("Agent_",a,"_lvl",lvl-1,"_distance")]][gen]
      
      baseline.gen3209 <- rbind.fill(baseline.gen3209,c(fitness=fit,distance=dist,generation=gen,level=lvl))
    }
  }
}
c <- data.frame(fitness=numeric(0),distance=numeric(0),generation=numeric(0),level=numeric(0))
ctable <- data.table(c)
for (gen in seq(1,nrow(sol.baseline.gen3210),1)){
  print(gen)
  for (a in seq(1,100,1)){
    for (lvl in seq(0,9,1)){
      fit <- sol.baseline.gen3210[[paste0("Agent_",a,"_lvl",lvl,"_fitness")]][gen]
      dist <- sol.baseline.gen3210[[paste0("Agent_",a,"_lvl",lvl,"_distance")]][gen]
             
      ctable <- rbindlist(list(ctable,as.list(c(fit,dist,gen,lvl))))
      }
  }
}

ctable <- data.table(c)
for (gen in seq(20)){
  print(gen)
  for (a in seq(1:100)){
    for (lvl in seq(1:10)){
      fit <- sol.baseline.gen3210[[paste0("Agent_",a,"_lvl",lvl-1,"_fitness")]][gen]
      dist <- sol.baseline.gen3210[[paste0("Agent_",a,"_lvl",lvl-1,"_distance")]][gen]
      
      ctable <- rbindlist(as.list(ctable,c(fit,dist,gen,lvl-1)))
      i = i+1
    }
  }
}