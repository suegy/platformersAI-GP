library(splitstackshape)
library(dplyr)
library(ggplot2)
library(gridExtra)
if (!require("gplots")) {
  install.packages("gplots", dependencies = TRUE)
  library(gplots)
}
if (!require("RColorBrewer")) {
  install.packages("RColorBrewer", dependencies = TRUE)
  library(RColorBrewer)
}


my_palette <- colorRampPalette(c("red", "yellow", "green"))(n = 299)

# (optional) defines the color breaks manually for a "skewed" color transition
col_breaks = c(seq(-1,0,length=100),  # for red
               seq(0,0.8,length=100),              # for yellow
               seq(0.8,1,length=100))              # for green
# Color housekeeping
library(RColorBrewer)
rf <- colorRampPalette(rev(brewer.pal(11,'Spectral')))
r <- rf(32)

gamalyzer$V101 <- NULL
gamalyzer <- do.call(data.frame,lapply(gamalyzer, function(x) replace(x, is.infinite(x),0.0)))

for (i in seq(1,100,1)){
  on <- paste0("V",i)
  nn <- paste0("Agent_",i)
  setnames(gamalyzer, old = on, new = nn )
  gamalyzer <- cSplit(gamalyzer, c(nn), c(":"))
  setnames(gamalyzer, old = paste0("Agent_",i,"_",1), new = paste0("Agent_",i,"_fitness"))
  setnames(gamalyzer, old = paste0("Agent_",i,"_",2), new = paste0("Agent_",i,"_similarity"))
  setnames(gamalyzer, old = paste0("Agent_",i,"_",3), new = paste0("Agent_",i,"_distance"))
  
}

c <- data.frame(fitness=numeric(0),similarity=numeric(0),distance=numeric(0),generation=numeric(0),level=numeric(0))
ctable <- data.table(c)
gen = c(seq(1,nrow(gamalyzer),1))
gamalyzer <- cbind(gamalyzer,gen)

for (ag in seq(1,100,1)){
  print(ag)
  agentfit <- paste0("Agent_",ag,"_fitness")
  agentdist <- paste0("Agent_",ag,"_distance")
  agentsim <- paste0("Agent_",ag,"_similarity")
  myCols <- c(agentfit,agentsim,agentdist,"gen")
  colNums <- match(myCols,names(gamalyzer))
  temp <- gamalyzer %>% select(colNums)
  lvlcol <- rep(0,nrow(temp))
  temp <- cbind(temp,lvlcol)
  ctable <- rbindlist(list(ctable,temp))
}
ctable$similarity[is.infinite(ctable$similarity )] = 0.0
ctable.s1.player2.add_distance$similarity <- sapply(ctable.s1.player2.add_distance$similarity, function(x) {x <- 1.0-x})

####
# plotting the evolutionary convergance of gamalyzer in fitness vs generation for multi fitness = similarity * distance
####
fitgenplot100 <- ggplot(ctable[ctable$generation < 100],aes(y=fitness,x=generation)) + geom_smooth(colour="blue") +ggtitle("100 generations")
fitgenplot1000 <- ggplot(ctable[ctable$generation < 1000],aes(y=fitness,x=generation)) + geom_smooth(colour="blue") +ggtitle("1000 generations")
fitgenplot2000 <- ggplot(ctable[ctable$generation < 2000],aes(y=fitness,x=generation)) + geom_smooth(colour="blue") +ggtitle("2000 generations")
fitgenplot5000 <- ggplot(ctable[ctable$generation < 5000],aes(y=fitness,x=generation)) + geom_smooth(colour="blue") +ggtitle("5000 generations")
fitgenplot6000 <- ggplot(ctable[ctable$generation < 6000],aes(y=fitness,x=generation)) + geom_smooth(colour="blue") +ggtitle("6000 generations")
fitgenplot7000 <- ggplot(ctable[ctable$generation < 7000],aes(y=fitness,x=generation)) + geom_smooth(colour="blue") +ggtitle("7000 generations")
grid.arrange(fitgenplot100,fitgenplot1000,fitgenplot2000,fitgenplot5000,fitgenplot6000,fitgenplot7000,ncol=3)
####
plots1p2_gama <- ggplot(ctable[ctable$generation < 3000],aes(y=fitness,x=distance)) + geom_smooth(colour="blue") +ggtitle("2000 generations")
ggplot(ctable[ctable$generation <50],aes(x=distance,y=fitness)) + geom_hex() + xlab("Distance") + ylab("Fitness") + scale_fill_gradientn(colours=my_palette) + coord_fixed(xlim =c(-10,300), ylim =c(-10,350))
hexbinplot(distance~fitness, data=ctable[ctable$generation <50], colramp=rf, trans=log, inv=exp, xlim =c(-10,300), ylim =c(-10,350))
hexbinplot(similarity~distance, data=ctable[ctable$generation <500][level == 0], colramp=rf, trans=log, inv=exp, xlim =c(-10,270), ylim =c(-0.5,1.5),aspect=1)
hexbinplot(fitness~distance, data=ctable[ctable$generation <10][level == 0], colramp=rf, trans=log, inv=exp, xlim =c(-5,20), ylim =c(-5,20))
ggplot(ctable[ctable$generation < 3000],aes(y=fitness,x=generation)) + geom_smooth()

> plot_baseline <- ggplot(data=ctable[ctable$generation <2000][level == 0], xlim =c(-5,2010), ylim =c(-5,270), aspect=1, aes(x=generation, distance)) + geom_point(aes(size=10,color = generation)) + geom_smooth( colour="black",size=1) +ggtitle("Baseline Evolution") + theme(legend.position="none") + coord_cartesian(ylim=c(0,270))
> 
  > plot_gama <- ggplot(data=ctable.s1.player2.add_distance[ctable.s1.player2.add_distance$generation <2000][level == 0], xlim =c(-5,2010), ylim =c(-5,270), aspect=1, main="blabla", aes(x=generation, distance)) + geom_point(aes(size=10,color = generation)) + geom_smooth( colour="black",size=1) +ggtitle("Player Based Evolution") + theme(legend.position="none") +coord_cartesian(ylim=c(0,270))
> 
  > grid.arrange(ncol=2, plot_baseline,plot_gama)
#####
## graph comparing two player similarities
#####
simgens1p2 <- ggplot(ctable.s1.player2.add_distance[ctable.s1.player2.add_distance$generation <2000],aes(x=generation,y=similarity)) + geom_smooth() + ggtitle("Training on Player A") +coord_fixed(ratio = 100000)
simgens1p4 <- ggplot(ctable[ctable$generation <2000],aes(x=generation,y=similarity)) + geom_smooth() + ggtitle("Training on Player B") +coord_fixed(ratio = 100000)
grid.arrange(ncol = 2, simgens1p2, simgens1p4)


####
# graph comparing the similarity to distance in 3 steps
####
hex1 <- hexbinplot(similarity~distance, data=ctable[ctable$generation <500][level == 0], colramp=rf, trans=log, inv=exp, xlim =c(-10,270), ylim =c(-0.5,1.5),aspect=1,main="10 Generations",colorkey = FALSE)
hex2 <- hexbinplot(similarity~distance, data=ctable.s1.player4.add_distance[ctable.s1.player4.add_distance$generation <100][level == 0], colramp=rf, trans=log, inv=exp, xlim =c(-10,270), ylim =c(-0.5,1.5),aspect=1,main="100 Generations",colorkey = FALSE)
hex3 <- hexbinplot(similarity~distance, data=ctable.s1.player4.add_distance[ctable.s1.player4.add_distance$generation <1000][level == 0], colramp=rf, trans=log, inv=exp, xlim =c(-10,270), ylim =c(-0.5,1.5),aspect=1,main="1000 Generations",colorkey = FALSE)
grid.arrange(ncol = 3, hex1, hex2, hex3)
