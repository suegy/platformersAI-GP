## Genetic programming extension for the platformerAI toolkit
============

The [platformersAI](http://www.platformersai.com/) toolkit offers a great way to toy around with Mario and the logic controlling the environment and the player in a 2D jump'n'run game setting.

This project focuses on the inclusion of a genetic programming componentent, [JGAP](https://github.com/suegy/JGAP), and the Gamalyzer metric into a platform game. the original JGAP was slightly extented to incorporate a better chromosome selector and also provdides a way switching on covariant parsimony pressure for tackling the chromosome length bloat.

### What you can do with this code
The GP extension used  here offers a chance to evolve artifical players for Mario as decision trees (DTs) from scratch. Those DTs are represented as java code and with a simple change can even be exported, modified and imported again. 

#### Where to start
You can just 'git clone' the project and give it a go to see how the agents learn from raw human controller input. You can switch the Genetic Selector, currently a balanced roulette wheel, or switch the fitness function against a staticial one. A baseline one is already provided. 

#### With Gamalyzer
You can record your own play, creating a trace, or collect them from others and allow Gamalyzer to create a similarity measure to compare them against t


### Related Research
[Gaudl, Osborn, Bryson , Learning from Play: Facilitating character design through genetic programming and human mimicry, EPIA2015](https://scholar.google.co.uk/citations?view_op=view_citation&hl=en&user=1P32b6gAAAAJ&sortby=pubdate&citation_for_view=1P32b6gAAAAJ:EUQCXRtRnyEC)
