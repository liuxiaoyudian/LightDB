Logic of extracting join conditions from Where clause: (The corresponding comments is located in the SelectExpressionDeParser class)
I create a class called JoinExpressionDeParser. This class extends the ExpressionDeParser class. This class has two Lists to store join and selection conditions respectively. The way to determine whether it is a join is to check whether the tableNames on both sides of the WHERE clause equation are the same. If they are not the same, it means that it is a join condition. Another method is to use PlainSelect.getJoins(), if the result is not null, it means that it is a join condition.




In terms of the implementation of Planer class (This class is used to create a query plan), first, I will judge whether a joinOperator is needed, if not, it will be simpler, just create the corresponding Operator in order. If joinOperator is needed, I will first process selection conditons, and than process join conditions. And JoinOperator will use the appropriate selectOperator as the left and right child. Therefore, this method is more effective, because we avoid the situation of calculating the cross product and making a selection later.