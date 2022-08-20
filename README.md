# 5782-ds-ass1-tomerandshelly
5782-ds-ass1-tomerandshelly created by GitHub Classroom

Credit: Part of the assignment was taken from lecturer Michael May from class exercises

Served:
Tomer Revivo-204470892, GitHub: TomerRevivo

Shelly Revivo-315661884, GitHub: yaminshelly

– A detailed list of which student worked on what and for approximately how many hours:

Tomer Revivo : Upload 20H, Download 10H, UploadToAll 3H.

Shelly Revivo: Lock 30H, Unlock 10H, List 5H, Connaction between Server and client(msg, exceptions) 10H.

Our system is a client server system. The system is capable of transferring data from a server computer to a client and also from many servers to many clients.

Run the program:

Running the server through jar:
1. Conference to cmd.
2. Type cd ...\multiServerClient\SentenceServerMulti\classes\artifacts\SentenceServerMulti_jar.
3. type: java -jar SentenceServerMulti.jar.
4. the configuration file of the server include server port and the file root directory to store file. the path of this configuration file is: ....\multiServerClient\SentenceServerMulti\classes\artifacts\SentenceServerMulti_jar\configureServer.txt
5. log file path tp the worker (hanler):...\multiServerClient\SentenceServerMulti\classes\artifacts\SentenceServerMulti_jar\MyLogHandler.log
6. one more log file to the server  :  ..\multiServerClient\SentenceServerMulti\classes\artifacts\SentenceServerMulti_jar\MyLogFileServer.log

Client Running:
1. Arrange the configuration file in the path: ..\multiServerClient\SentenceClient\classes\artifacts\SentenceClient_jar\FileConfigurationCsv.csv
2. Add the servers by name:serverOne, serverTwo, serverThree.
3. Add a suitable ip to each server.
4. Save the configuration file as csv (comma delimited).

User interface:
In the user interface we will have buttons of lock, unlock, version, uplaoad, download, list, lock for all , upload to all , version to all , disconnect . 

How each button works:

1.Version- In order to get a version of a file, the name of the file must be written in the text box next to the vesion, in addition we must make sure that we are in the correct server name. (the same action to the version to all ) 

2. lock- In order to lock a file, select it from the list of files on the server. (the same action to the lock for all ) 
 
3. unlock- In order to release a file one has to select it from the list of files on the server.(The same action for unlock to all ) 
 
4. list- In order to get a list of files on a particular server, one must select the server that we are checking, first.

5. upload- In order to upload a file to the server, select a file by clicking the browse button and only then click upload. (The same action to upload to all ) 

how thread safety is ensured:

We use thread safe collection- ConcurrentHashMap - for the file index . 
The lock ConcurrentHashMaps save the name of the file with the ip of the client so no one can unlock file that locked already by someone else. when file locked, it locked the file on all the server connect. The same story about unlock, bun anlock remove files from the hush map of Lockfiles . 
About the upload and versions , we save the version of the files in a hash map too, when file upload , if he does not exist it put to the hash map with his version , when its exist in this server it overited the file exist  (just if the file is locked) so the version always the newer one. It always save the new version of thr file . 
this is the thread safety of our project .

