
#binning items for graph on the correlation between distance and fitness
library(hexbin)

simDistData <- do.call(data.frame,lapply(simDistData, function(x) replace(x, is.infinite(x),0.0)))
hexbinplot(dist~fit,data=simDistData,colramp=rf,trans=log,inv=exp)
bin <- hexbin(simDistData,xbins=60)
hexbinplot(dist~fit,data=simDistData,colramp=rf,trans=log,inv=exp)
hexbinplot(data=simDistData,colramp=rf,trans=log,inv=exp)

### figure for showing the comparision of reaching the goal over generations
library(ggplot2)
ggplot(simDistData[simDistData$dist == 256,],aes(x=which(simDistData$dist == 256),y=fit)) + xlab("generations reaching the goal") + ylab("fitness") + stat_binhex()

ggplot(simDistData[seq(1:100),],aes(x=dist,y=fit)) + geom_hex() + xlab("distance")+ ylab("fitness")+ scale_fill_gradientn(colours=r)