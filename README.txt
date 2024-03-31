cmd -> Changes PowerShell to cmd
javac -d . *.java -> turns all .java in directory in a folder
java -cp . hello.HelloServer -> runs the server
open another cmd and run:
java -cp . hello.HelloClient -> runs the client
exit to exit cmd into powershell



javac -cp ".;lib/*" -d bin src/project/*.java src/project/interfaces/*.java src/project/resources/*.java src/project/servers/*.java
java -cp "bin;lib/*" project.<main_class_name_here>