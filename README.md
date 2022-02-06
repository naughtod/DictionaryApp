# DictionaryApp
This app allows multiple clients to be able to read/write to a dictionary managed by a single multi-threaded server and perform the following operations,

1. Query the meaning(s) of a word
2. Add a new word and corresponding meaning(s)
3. Delete an existing word and its meaning(s)
4. Update the meaning(s) for an existing word

See report1.pdf in the repo for the full list of features and explanation of how the app works.

## Run to start the server 
    java -jar DictionaryServer.jar <port> data/tinydata.csv
## Run to start a client
    java -jar DictionaryClient.jar <server-address> <server-port>
Can test locally by using unused port between 1024 and 65353 and setting server-address to localhost  
