# Lerat-Lambert Tree FTP

* **Auteur** : Paul Lerat-Lambert.
* **Date de création** : 14/01/2021.
* **Description** : Ce dépôt correspond au rendu du premier projet de l'UE Systèmes Répartis 1.

## Tags :

Plusieurs versions progressives du dêpot sont disponibles. Charger une version avec :  
`git checkout **tag**`

où tag peut prendre comme valeur :  

* `master` : Pour accéder à la dernière version du dépôt.
* `firstWeek` : Pour accéder à la version du dépôt correspondant a la fin de la première semaine de travail.
* `secondWeek` : Pour accéder à la version du dépôt correspondant a la fin de la deuxième semaine de travail.
* `thirdWeek` : Pour accéder à la version du dépôt correspondant a la fin de la troisième semaine de travail.
* `renduFinal` : rendu final.


## Make : 

Ce dépot contiens un **Makefile** afin de faciliter l'usage. Liste des commandes implémentées : 

* `make javadoc`        : Crée la javadoc dans le fichier **./docs/javadoc/**.
* `make exec`           : Crée l'executable.
* `make notest`         : Crée l'executable sans passé par les tests.
* `make clean`          : Nettoie le projet.

## How to run !

Pour run le programme il suffi de créer l'executable, puis executer le commande suivante :
`java -jar ./target/TreeFTP-1.0-SNAPSHOT.jar serverAddress [options]`

Avec options : 
```bash
Options :
-u <String> : your login
-p <String> : your password
-c : set color mode at true
-d <int> : max depth to reach
```
## I : Introduction

L'objectif du projet est de mettre en œuvre un commande shell permettant d'afficher sur la sortie standard d'un terminal  l'arborescence d'un répertoire distant accessible via le protocole applicatif **File Transfer Protocol (FTP)**. Le rendu de l'arborescence distante s'inspirera du formalisme utilisé la commande tree de Linux.  

L'execution du programme se découpe donc en trois parties :

* **1** : Connexion au serveur ftp.
* **2** : Execution de la commande tree, en utilisant des commandes FTP.
* **3** : Déconnexion du serveur ftp.

Durant toutes l'execution, le programmes **garde une trace** des commandes executées et des réponses reçues. Cette trace est alors écrite dans le fichier `tree.log`

### Connexion et Deconnexion FTP : 

Pour la connexion et la doconnexion au FTP, le programme se repose sur l'utilisation **socket**. En effet lors de la connexion une intialisation de `Java.Socket` avec comme arguments l'addresses données en paramètre et 21 comme port.  
Par la suite une fois l'initalisation effective, le programme récupère les "Objets" dont il aura besoin : 
* **DataOutputStream**  : flux de communication client -> server.
* **DataInputStream**   : flux de communication server -> client.

Puis grace à ces **stream** il initialise des **BufferedReader** et **BufferedWriter** afin de simplifier la communication avec le client.

Connernant la deconnexion elle se passe en deux étape :
* **1** : Prévenir le serveur de notre déconnecion imminente.
* **2** : Fermeture du socket.

### Communication avec le serveur :

Grâce au reader et writer initialisés lors de la connection, le programme est maintenant capable de communiquer avec le seveur, notamment grâces aux méthodes `sendMessageToServer` et `getResponseFromServer`. Pour se faire il se base sur un **EnumCommand** dans lequel sont gardés en mémoire toutes les commandes implementées pour ce projet ainsi que la réponse attendu après leur appels.

Le Fonctionnement est alors simple (`sendCommand`): le programme envoie la commande `EnumCommand.name()`, récupère ensuite la réponse et la compare avec la réponse attendue `EnumCommand.getExpectedResponse()`.

### Algorithme tree : 

Notre agorithme de **tree** est basé sur le principe d'un parcours en profondeur :
* **A** : Liste tous les élements fils.
* **B** : Pour chaque éléments.
    * **B.1** : l'affiche avec l'identation correct indexée par la profondeur. 
    * **B.2** : Si l'element est un répertoire/ 
        * **B.2.1** : On se déplace dans ce répertoire *(dive)*.
        * **B.2.1** : Effecture un appels récursif en incrémentant la profondeur.
* **C** : Une fois tous les élements parcouru on "remonte" dans le répertoire parrant *(climb)*.

## II : Architecture 

*Lister et expliquer ici les interfaces, les classes abstraites et les méthodes polymorphiques*

### Arbosence de nos sources :

    src/main/
    └── java
        └── fil
            └── sr1
                └── leratlambert
                    ├── App.java
                    ├── client
                    │   ├── ClientFTP.java
                    │   ├── Client.java
                    │   ├── PassiveModeClient.java
                    │   ├── TreeFTPClient.java
                    │   └── utils
                    │       └── EnumCommand.java
                    └── tree
                        ├── TreeFTP.java
                        └── Tree.java

L'artchitécture de notre projet se sépare en deux parties : 

* **1** : Client
* **2** : Tree.



### Clients 

Les clients se repose sur un **Interface Client.java**. Cet Interface représente un client dans le modèle Server/Client. Il dispose donc de méthodes pour :

* Se connecter.
* Se deconnecter.
* Envoyer un messager au serveur.
* Lire un message du serveur.
* Tester sa connection.

Il existe alors trois types de clients :
* `ClientFTP`, simule un clientFTP.
* `TreeClientFTP`, extends `ClientFTP` en y ajoutant des paramètres nécessaire pour le bon fonctionnement du projet ainsi que l'implémentation des commandes FTP qui seront utilisées lors des appels.
* `PassivModeClient`, client utilisé lors de la connection en mode passive.

### Tree

La classe `TreeFTP` est celle qui gère l'algorithme de Tree, elle implémente *L'interface Tree* qui définir les methodes nécessaire à l'algorithme : 
* Dive, plonge dans un répertoire.
* Clim, remonte dans le répertoire parent.
* Affichage
* Run, implémentation de l'algorithme.

De plus `TreeFTP`possède un paramètre de type `TreeClientFTP` afin de communiquer avec le serveur choisi. 

## III : Code Samples 
*Ensuite, le readme liste 5 exemples de code (code samples) intéressants*

### Envoie d'une commande
```java
public enum EnumCommand {

    /** CONNECT : Expected response : 220 */
    CONNECT("220"),
    ...
    /** NOOP : exepeced response : 200. Used for test */
    NOOP("200");
```
```java
/**
     * Send a command to the server
     * @param command, the command to send.
     * @param data, if the command need some data, can be null.
     * @return <code>true</code> if the command was successful.
     *         <code>false</code> otherwise.
     * @throws IOException
     */
    public boolean sendCommand(EnumCommand command, @Nullable String data) throws IOException {
        assert isConnect();
        if (data == null) {
            this.sendMessageToServer(command.name());
            logger.info("--> " + command.name());

        } else {
            this.sendMessageToServer(command.name() + " " + data);
            logger.info("--> " + command.name() + " " + data);
        }
        this.getResponseFromServer();
        if (this.response != null && this.response.startsWith(command.getExpectedResponse())) { /* Check if we get the expected response */
            return true;
        }
        else {
            return false;
        }
    }
    ...
    public boolean sendCommand(EnumCommand command) throws IOException {
        return this.sendCommand(command, null); /* Calling sendCommand with null as data */
    }
```
### PASV
```java
/**
         * Execute the FTP command : PASV, using EnumCommand.PASV and methode sendCommand inherited from ClientFTP.
         * It also connect our PassiveModeClient to the ip and port given by the server.
         * @return <code>true</code> if the command was successful and the PassiveModeClient successfully connected..
         *         <code>false</code> otherwise.
         * @throws IOException
         */
        public boolean PASV() throws IOException {
            if (this.sendCommand(PASV)) {
                Pattern pattern = Pattern.compile("(\\d+,)+\\d+");  /* Pattern regex to get only the number for ip and port. */
                Matcher matcher = pattern.matcher(this.response);
                if (matcher.find()) {
                    List<String> myList = new ArrayList<String>(Arrays.asList(matcher.group().split(","))); /* Splitting all number into a list */
                    int i = 0;
                    int portPassiveMode = 0;
                    String ipPassiveMode = new String();
                    for(String number : myList) {
                        if(i < 4){
                            ipPassiveMode = ipPassiveMode + number + "."; /* Generate ip */
                        }
                        else if (i == 4){
                            ipPassiveMode = ipPassiveMode.substring(0, ipPassiveMode.length() - 1); /* removing the last . */
                            portPassiveMode = Integer.parseInt(number) * 256;   /* port = 256 * mylist[4] + myList[5] */
                        }
                        else {
                            portPassiveMode = portPassiveMode + Integer.parseInt(number);   /* port = 256 * mylist[4] + myList[5] */
                        }
                        i = i + 1;
                    }
                    this.passiveModeClient.connect(ipPassiveMode, portPassiveMode, this.login, this.password);  /* Connecting our passive mode client with ip and port given by pasv command  */
                    if (this.passiveModeClient.isConnect()) { /* Check if connection going well */
                    } else {
                        System.out.println("Connection passive mode failed");
                    }
                    return(this.passiveModeClient.isConnect());
                }
            }
            return false;
        }

```

###  Algorithme de Tree
```java
/**
     * Run the tree command.
     * @throws IOException
     */
    @Override
    public void run(int depth, boolean parentDirIsLast) throws IOException {
        assert this.client.isConnect();
        this.client.PWD();  /* We must PWD to get the good current dir */
        String currentDir = this.client.getCurrentDir();
        this.client.PASV(); /* We must PASV before LIST */
        this.client.LIST();
        List<String> listN = transformListToGetOnlyFileName(this.client.getListCurrentDir());
        if (this.maxDepth < 0 || depth < this.maxDepth ) {
            for (String name :  listN) {
                boolean isLast = listN.indexOf(name) == listN.size() -1;    /* Check if the current file or directory is the last from his directory */
                if (this.dive(currentDir + '/' + name)) {   /* dive into new directory */
                    this.printWithIdent(name, depth, ANSI_BLUE, isLast, parentDirIsLast); /* DIRECTORY */
                    this.run(depth + 1, isLast);
                } else {
                    if (name.contains("->")) {  /* SYMBOLIC LINK */
                        this.printWithIdent(name, depth, ANSI_CYAN, isLast, parentDirIsLast);
                    } else {    /* FILE */
                        this.printWithIdent(name, depth, ANSI_RESET, isLast, parentDirIsLast);
                    }
                }
            }
        }
        this.climb();   /* We can climb when we have browse all file in our current directory */
    }
```

### Affichage des fichiers avec gestion des couleurs 
```java
/**
     * Print file or directory send as parameters.
     * @param fileOrDirName, name to print.
     * @param depth, depth of the file or directory.
     * @param color, color to print.
     * @param isLast, boolean to print the good prefix of indentation.
     */
    @Override
    public void printWithIdent(String fileOrDirName, int depth, String color, boolean isLast, boolean parentDirIsLast) {
        String prefix = new String();
        for (int i = 0; i < depth; i = i + 1) {
            if (parentDirIsLast) {  /* Choose good prefix */
                prefix = prefix + " ";
            }
            else {
                prefix = prefix + "│";
            }
            prefix = prefix + "\t";
        }
        prefix = prefix + (isLast ? "└── " : "├── "); /* Choose good prefix */
        if (this.colorMode) {
            System.out.println(prefix + color + fileOrDirName + ANSI_RESET);
        }
        else {
            System.out.println(prefix + fileOrDirName);
        }
    }
``` 

### Test du bon affichage de Tree
```java
package fil.sr1.leratlambert.tree;

import fil.sr1.leratlambert.client.ClientFTP;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Class Test for TreeFTP
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TreeFTPTest {
    ...
    private static String EXPECTED_OUTPUT_1 = "├── test1\n" +
            " \t \t├── test2\n" +
            "│\t│\t└── test3\n";
    private static String EXPECTED_OUTPUT_2 = [...]
    private static String EXPECTED_OUTPUT_3 = [...]
    ...
    @BeforeAll
    public static void setUp() throws IOException {
        tree = new TreeFTP("ftp.ubuntu.com", 21, null, null, false, 1);
        oldOut = System.out; /* Memorise old System.out */
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream)); /* Set outputStream as output for print method */
    }
    ...
    @Test
    @BeforeEach
    public void beforeEach() throws IOException {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }
    ...
    /**
     * Test for printWithIdent.
     */
    @Test
    @Order(3)
    public void testPrintWithIdent() {
        tree.printWithIdent("test1", 0, "", false, false);
        tree.printWithIdent("test2", 2, "", false, true);
        tree.printWithIdent("test3", 2, "", true, false);
        String output = new String(outputStream.toByteArray());
        assertEquals(output, EXPECTED_OUTPUT_1);
    }

    /**
     * Test for run.
     * @throws IOException
     */
    @Test
    @Order(4)
    public void testRun() throws IOException {
        assert tree.getClient().isConnect();
        tree.run(0, false);
        String output = new String(outputStream.toByteArray());
        assertEquals(output, EXPECTED_OUTPUT_2);    }

    ...

    /**
     * Test exec.
     * @throws IOException
     */
    @Test
    @Order(6)
    public void testExec() throws IOException {
        assert tree.getClient().isConnect();
        ClientFTP c= tree.getClient();
        tree.exec();
        assertFalse(c.isConnect());
        String output = new String(outputStream.toByteArray());
        assertEquals(output, EXPECTED_OUTPUT_3);
    }
}
``` 



## IV : Bibliographies 
* [Sujet](https://moodle.univ-lille.fr/mod/page/view.php?id=640539)
* [Consignes Rendu](https://moodle.univ-lille.fr/mod/page/view.php?id=644738)
* [Replace DataInutStream.readline()](http://www.eg.bucknell.edu/~mead/Java-tutorial/post1.0/converting/deprecatedIO.html)
* [How to Convert Code that Uses I/O ](http://www.eg.bucknell.edu/~mead/Java-tutorial/post1.0/converting/convertingIO.html)
* [Why use Buffer Reader/Writer](https://medium.com/@isaacjumba/why-use-bufferedreader-and-bufferedwriter-classses-in-java-39074ee1a966)
* [Ordonnance des test](https://www.softwaretestinghelp.com/juni-test-execution-order/)
* [StringTokenizer](http://b.kostrzewa.free.fr/java/td-chaines/tokenize.html)
* [Test print method](https://stackoverflow.com/questions/32241057/how-to-test-a-print-method-in-java-using-junit)
* [Write into logfile](https://stackoverflow.com/questions/15758685/how-to-write-logs-in-text-file-when-using-java-util-logging-logger)
