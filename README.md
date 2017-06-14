# Transaction Manager for Policies in TIPPERS
Course project for TDM CS223  
Contributors: @nishadg @YadhuPrakash @ChaitanyaKshirsagar

## Setup Requirements
The project needs MySQL to be running. Database config file is located in `src/main/resources/db.config.properties`. Edit this file for the project to connect to the database.


## Features
The transaction manager can perform standard commit and abort operations. It can also handle failures and performs appropriate undo/redo operations while recovering.
The Transaction manager maintains log entries for every operation on the policies table which is then used to recover in case of failure.

The project also supports time traversal, i.e. allowing the users to view the state of the database at a particular time in the past. This is particularly useful when auditing policies and their impact. We achieve this by maintaing entered and invalidated timestamps for every policy essentially making it a temporal database.

## API

1. Create Transaction

   Use the `TransactionManager` class to get an instance of the `Transaction` class which can then be used to perform operations.  
   `Transaction transaction = TransactionManager.createTransaction();`

2. Begin Transaction

   Call begin on the `Transaction` object. This will initialize buffers and create connections to the database.  
   `transaction.begin();`
   
3. Insert/Update a Policy

   To insert a policy call write on the `Transaction` object and pass in the JSON of the policy.  
   `trasaction.write(policyJSON);`
   
   A typical policy JSON would be  
   ```
   {
   "policyID": 0,
   "fromTS": "Jun 8, 2017 5:56:22 PM",
   "toTS": "Aug 5, 2013 6:19:03 PM",
   "author": "Nishad Gurav",
   "querier": "Yadhu Prakash"
   }
   ```
   
   Note that if you pass in a policy with an ID that already exists, the policy will be updated.
   
  
4. Commit/Abort Transaction
   
   Calling commit or abort on a transaction will persist the changes made and make it permanent.
   `transction.commit();` / `transaction.abort();`
   
5. Time Traversal

   Time traversal takes in a `java.sql.Timestamp` object as a parameter and returns a string containing a JSON array of the policies that were active at the provided timestamp.  
   `String policies = transaction.timeTraversal(timestamp);`
   
   
