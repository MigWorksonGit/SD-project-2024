cmd -> Changes PowerShell to cmd
javac -d . *.java -> turns all .java in directory in a folder
java -cp . hello.HelloServer -> runs the server
open another cmd and run:
java -cp . hello.HelloClient -> runs the client
exit to exit cmd into powershell



javac -cp ".;lib/*" -d bin src/project/*.java src/project/interfaces/*.java src/project/resources/*.java src/project/servers/*.java
java -cp "bin;lib/*" project.GatewayServer PORT_NUMBER MULTICAST_ADDRESS MULTICAST_PORT
java -cp "bin;lib/*" project.Client IP_ADDRESS PORT_NUMBER
java -cp "bin;lib/*" project.Downloader IP_ADDRESS PORT_NUMBER
java -cp "bin;lib/*" project.barrel IP_ADDRESS PORT_NUMBER

Example:
java -cp "bin;lib/*" project.GatewayServer 1099 230.0.0.1 4446
java -cp "bin;lib/*" project.Client localhost 1099
java -cp "bin;lib/*" project.Downloader localhost 1099
java -cp "bin;lib/*" project.barrel localhost 1099

Webpage has: url, title, citation, number_of_fathers
I want to send:
word,
Index: word, List<url, title, citation, number_of_fathers> -> word, List<Webpage>

Properties "file" :
endrreço da máquina
porto dos barrel
portos dos downaloder
porto dos clientes

