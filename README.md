# DeepLearningAndDataMiningRepository

In this project I used a database of congress votes.
This is the link to the database and the explanation about it:
http://archive.ics.uci.edu/ml/datasets/Congressional+Voting+Records

There are 435 votes in total; 
Each candidate is either a republican or a democrat.
Each candidate voted on 16 different votes maximum.
The first column in the database represents the candidate {Republica/Democrat},
the other 16 columns represent the votes; Each vote is either y for Yes, n for No and ? for Abstain.

I calculated the entropy for the total democrats and republicans, then I calculated each information-gain per each attribute.
I took the maximum info-gain and constructed a one-level decision tree; The tree has 90% accuracy, 
according to 10 different random lines in the database.
