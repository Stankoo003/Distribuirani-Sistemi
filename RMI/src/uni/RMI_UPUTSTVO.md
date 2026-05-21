# Java RMI – Uputstvo za kreiranje aplikacija

Ovaj dokument opisuje tačan način kreiranja Java RMI aplikacija na osnovu fakultetskih primera.

---

## Sadržaj

1. [Osnovna RMI aplikacija](#1-osnovna-rmi-aplikacija)
2. [RMI sa Callback mehanizmom](#2-rmi-sa-callback-mehanizmom)
3. [Callback sa mapom klijenata (Chat)](#3-callback-sa-mapom-klijenata-chat)
4. [Pravila i obrasci koji se uvek primenjuju](#4-pravila-i-obrasci-koji-se-uvek-primenjuju)

---

## 1. Osnovna RMI aplikacija

**Primer:** `CalculatorAcc` – kalkulator sa akumulatorom.

### Struktura fajlova

```
NazivAplikacije/
├── NazivServisa.java        (Remote interfejs)
├── NazivServisaImpl.java    (Implementacija)
├── Server.java
└── Client.java
```

### Korak 1 – Remote interfejs

```java
import java.rmi.*;

public interface CalculatorAcc extends Remote {
    public int add(int a) throws RemoteException;
    public int sub(int a) throws RemoteException;
    public int mul(int a) throws RemoteException;
    public int div(int a) throws RemoteException;
    public int getAcc() throws RemoteException;
}
```

**Pravila:**
- Interfejs mora da `extends Remote`
- Svaka metoda mora da baca `throws RemoteException`
- Samo `import java.rmi.*` je potreban

---

### Korak 2 – Implementacija (`*Impl`)

```java
import java.rmi.*;
import java.rmi.server.*;

public class CalculatorAccImpl extends UnicastRemoteObject implements CalculatorAcc {

    private int acc;

    protected CalculatorAccImpl() throws RemoteException {
        super();
        acc = 0;
    }

    @Override
    public int add(int a) throws RemoteException {
        acc += a;
        return acc;
    }

    // ... ostale metode
}
```

**Pravila:**
- Klasa mora da `extends UnicastRemoteObject`
- Mora da `implements` Remote interfejs
- Konstruktor mora biti `protected`, mora pozivati `super()`, mora bacati `throws RemoteException`
- Importi: `java.rmi.*` i `java.rmi.server.*`

---

### Korak 3 – Server

```java
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.*;

public class Server {

    public Server(String objectName) {
        // Kreiranje registry-a (ili detekcija postojećeg)
        try {
            LocateRegistry.createRegistry(1099);
            System.out.println("Java RMI registry created.");
        } catch (RemoteException e) {
            System.out.println("Java RMI registry already exists.");
        }

        // Kreiranje i registracija objekta
        try {
            CalculatorAccImpl c = new CalculatorAccImpl();
            Naming.rebind("rmi://localhost:1099/" + objectName, c);
        } catch (RemoteException e) {
            System.out.println("Failure during RMI object creation: " + e);
        } catch (MalformedURLException e) {
            System.out.println("Failure during Name registration: " + e);
        }
    }

    public static void main(String[] args) {
        String objectName = args[0];
        new Server(objectName);
        System.out.println("Server started.");

        try {
            System.in.read(); // Drži server aktivnim
        } catch (IOException e) {}
    }
}
```

**Pravila:**
- `LocateRegistry.createRegistry(1099)` – uvek port `1099`
- Ako registry već postoji, `RemoteException` se hvata i ignoriše
- `Naming.rebind("rmi://localhost:1099/" + objectName, objekat)` – registracija
- `System.in.read()` na kraju drži JVM proces živ (server se ne gasi)
- Ime objekta se prima kao `args[0]`

---

### Korak 4 – Client

```java
import java.net.MalformedURLException;
import java.rmi.*;
import java.util.Scanner;

public class Client {
    public static void main(String args[]) {
        String objectName = args[0];
        Scanner scanner = new Scanner(System.in);

        CalculatorAcc calculatorAcc;
        try {
            // Lookup – dobijanje reference na udaljeni objekat
            calculatorAcc = (CalculatorAcc) Naming.lookup("rmi://localhost:1099/" + objectName);

            while (true) {
                String op = scanner.nextLine();
                if (op.equals("+")) calculatorAcc.add(5);
                // ...
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

        scanner.close();
    }
}
```

**Pravila:**
- `Naming.lookup(...)` vraća `Remote`, kastuje se u tip interfejsa
- Hvata se: `MalformedURLException`, `RemoteException`, `NotBoundException`
- Ime objekta je `args[0]`, mora se poklapati sa onim što server registruje

---

## 2. RMI sa Callback mehanizmom

**Primer:** `CalculatorAccCallback` – server obaveštava sve klijente kad se acc promeni.

### Struktura fajlova

```
NazivAplikacije/
├── NazivServisa.java        (Remote interfejs servisa – dodati register/unregister)
├── NazivServisaImpl.java    (Implementacija – čuva listu callback-ova)
├── Callback.java            (Remote interfejs za callback)
├── Server.java              (identičan kao osnovna verzija)
└── Client.java              (kreira CallbackImpl kao unutrašnju klasu)
```

### Korak 1 – Callback interfejs

```java
import java.rmi.*;

public interface Callback extends Remote {
    public void accChanged() throws RemoteException;
}
```

**Pravila:**
- Ovo je novi Remote interfejs koji implementira **klijent**, ne server
- Metoda predstavlja notifikaciju – server je poziva na klijentu

---

### Korak 2 – Prošireni Remote interfejs servisa

```java
import java.rmi.*;

public interface CalculatorAcc extends Remote {
    public void add(int a) throws RemoteException;
    public void sub(int a) throws RemoteException;
    // ...
    public int getAcc() throws RemoteException;
    public void register(Callback cb) throws RemoteException;     // NOVO
    public void unregister(Callback cb) throws RemoteException;   // NOVO
}
```

**Napomena:** Metode koje menjaju stanje (add, sub...) postaju `void` jer server sada šalje rezultat kroz callback, ne return vrednost.

---

### Korak 3 – Implementacija sa callback listom

```java
import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;

public class CalculatorAccImpl extends UnicastRemoteObject implements CalculatorAcc {

    private ArrayList<Callback> clients = new ArrayList<Callback>(); // lista klijenata
    private int acc;

    protected CalculatorAccImpl() throws RemoteException {
        super();
        acc = 0;
    }

    @Override
    public void add(int a) throws RemoteException {
        acc += a;
        callCallbacks(); // poziva sve registrovane klijente
    }

    @Override
    public synchronized void register(Callback cb) throws RemoteException {
        clients.add(cb);
    }

    @Override
    public synchronized void unregister(Callback cb) throws RemoteException {
        clients.remove(cb);
    }

    private void callCallbacks() {
        try {
            for (Callback cb : clients) {
                cb.accChanged(); // RMI poziv ka klijentu
            }
        } catch (Exception e) {}
    }
}
```

**Pravila:**
- `register` i `unregister` su `synchronized` (višenitni pristup listi)
- `callCallbacks()` je `private`, poziva se interno posle svake promene stanja
- Iteracija po listi i poziv callback metode klijenata

---

### Korak 4 – Client sa unutrašnjom CallbackImpl klasom

```java
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class Client {
    private CalculatorAcc calculatorAcc;
    private Callback cb;

    public Client(String objectName) {
        Scanner scanner = new Scanner(System.in);
        try {
            calculatorAcc = (CalculatorAcc) Naming.lookup("rmi://localhost:1099/" + objectName);
            cb = new CallbackImpl();           // kreiranje callback objekta
            calculatorAcc.register(cb);        // registracija na serveru

            while (true) {
                String op = scanner.nextLine();
                if (!op.equals("+") /* ... */) break;
                int number = Integer.parseInt(scanner.nextLine());
                calculatorAcc.add(number);
            }

            calculatorAcc.unregister(cb); // odjava pre izlaska
        } catch (Exception e) {}
        scanner.close();
    }

    public void getAcc() {
        try {
            System.out.println("acc = " + calculatorAcc.getAcc());
        } catch (Exception e) {}
    }

    public static void main(String args[]) {
        new Client(args[0]);
    }

    // Unutrašnja klasa koja implementira Callback interfejs
    public class CallbackImpl extends UnicastRemoteObject implements Callback {

        protected CallbackImpl() throws RemoteException {
            super();
        }

        @Override
        public void accChanged() throws RemoteException {
            getAcc(); // poziva metodu spoljne klase
        }
    }
}
```

**Ključne karakteristike Client+Callback obrasca:**
- `CallbackImpl` je **unutrašnja klasa** unutar `Client`
- `CallbackImpl extends UnicastRemoteObject implements Callback` – ista struktura kao serverska implementacija
- Unutrašnja klasa poziva metode spoljne `Client` klase (`getAcc()`)
- Redosled u konstruktoru: `lookup` → `new CallbackImpl()` → `register(cb)`
- Na kraju petlje obavezno `unregister(cb)`
- Atributi `calculatorAcc` i `cb` su polja klase, ne lokalne promenljive

---

## 3. Callback sa mapom klijenata (Chat)

**Primer:** `Chat` – privatna razmena poruka između klijenata.

### Razlika u odnosu na listu: HashMap umesto ArrayList

Kada klijenti imaju identitete (username), koristi se `HashMap<String, Callback>` umesto `ArrayList<Callback>`.

### ChatCallback interfejs

```java
import java.rmi.*;

public interface ChatCallback extends Remote {
    public void onMessageReceived(String usernameFrom, String message) throws RemoteException;
}
```

### ChatServer interfejs

```java
import java.rmi.*;

public interface ChatServer extends Remote {
    public void sendMessage(String usernameFrom, String usernameTo, String message) throws RemoteException;
    public void register(String username, ChatCallback cb) throws RemoteException;
    public void unregister(String username) throws RemoteException;
}
```

**Napomena:** `register` prima i `String username` i `ChatCallback cb` – klijent se identifikuje imenom.

### ChatServerImpl

```java
import java.rmi.*;
import java.rmi.server.*;
import java.util.HashMap;

public class ChatServerImpl extends UnicastRemoteObject implements ChatServer {
    HashMap<String, ChatCallback> callbacks; // mapa: username -> callback

    public ChatServerImpl() throws RemoteException {
        super();
        callbacks = new HashMap<>();
    }

    public void sendMessage(String usernameFrom, String usernameTo, String message) throws RemoteException {
        if (!usernameExists(usernameTo) || !usernameExists(usernameFrom)) {
            System.out.println("Wrong username.");
            return;
        }
        ChatCallback cb = callbacks.get(usernameTo); // pronalazak primaoca
        cb.onMessageReceived(usernameFrom, message); // RMI poziv ka klijentu-primaocu
    }

    public synchronized void register(String username, ChatCallback cb) throws RemoteException {
        if (usernameExists(username)) return; // sprečava duplikate
        callbacks.put(username, cb);
    }

    public synchronized void unregister(String username) throws RemoteException {
        if (!usernameExists(username)) return;
        callbacks.remove(username);
    }

    private boolean usernameExists(String username) {
        return callbacks.containsKey(username);
    }
}
```

### Chat Client

```java
import java.rmi.*;
import java.rmi.server.*;
import java.util.Scanner;

public class Client {
    private ChatCallback cb;
    private ChatServer cs;
    private String username;

    public Client(String chatName, String username) {
        try {
            cs = (ChatServer) Naming.lookup("rmi://localhost:1099/" + chatName);
            cb = new ChatCallbackImpl();
            cs.register(username, cb); // registracija sa username-om
            this.username = username;
        } catch (Exception e) {}
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                String input = scanner.nextLine();
                if (!input.equals("s")) break;

                String usernameTo = scanner.nextLine();
                String message = scanner.nextLine();
                cs.sendMessage(username, usernameTo, message);
            }
            cs.unregister(username);
        } catch (Exception e) {}
        scanner.close();
    }

    private void showMessage(String usernameFrom, String message) {
        System.out.println(usernameFrom + ": " + message);
    }

    public static void main(String[] args) {
        new Client(args[0], args[1]).run(); // args[0]=chatName, args[1]=username
    }

    public class ChatCallbackImpl extends UnicastRemoteObject implements ChatCallback {
        public ChatCallbackImpl() throws RemoteException { super(); }

        public void onMessageReceived(String usernameFrom, String message) throws RemoteException {
            showMessage(usernameFrom, message);
        }
    }
}
```

### Chat Server

```java
import java.rmi.*;
import java.rmi.registry.*;

public class Server {
    public Server(String chatName) {
        try {
            ChatServer cs = new ChatServerImpl();
            LocateRegistry.createRegistry(1099);
            Naming.rebind(chatName, cs); // bez "rmi://localhost:1099/" prefiksa - oba načina rade
            System.in.read();
        } catch (Exception e) {}
    }

    public static void main(String args[]) {
        new Server(args[0]);
    }
}
```

---

## 4. Pravila i obrasci koji se uvek primenjuju

### Importi po tipu klase

| Klasa | Importi |
|---|---|
| Remote interfejs | `java.rmi.*` |
| `*Impl` klasa | `java.rmi.*`, `java.rmi.server.*` |
| Server | `java.rmi.*`, `java.rmi.registry.*`, `java.net.MalformedURLException`, `java.io.IOException` |
| Client | `java.rmi.*`, `java.net.MalformedURLException` |
| Client sa callback-om | `java.rmi.*`, `java.rmi.server.UnicastRemoteObject` |

---

### Registry i Naming – dva načina

```java
// Način 1 – pun URL (eksplicitno)
Naming.rebind("rmi://localhost:1099/" + objectName, obj);
Naming.lookup("rmi://localhost:1099/" + objectName);

// Način 2 – samo ime (implicitno localhost:1099)
Naming.rebind(objectName, obj);
Naming.lookup(objectName);
```

Oba načina rade. Fakultetski primeri koriste oba – konzistentno koristi jedan.

---

### Pokretanje aplikacije (redosled)

```bash
# 1. Prevedi sve fajlove
javac *.java

# 2. Pokreni server (prima objectName kao argument)
java Server myCalc

# 3. Pokreni klijenta (mora koristiti isto ime)
java Client myCalc

# Za Chat (dva argumenta)
java Client myChat alice
java Client myChat bob
```

---

### Sinhronizacija

- `register` i `unregister` uvek `synchronized` – više klijenata može se istovremeno registrovati
- Metode koje samo čitaju stanje (`getAcc`, `sendMessage`) ne moraju biti `synchronized`
- `callCallbacks` nije `synchronized` – poziva se unutar metode koja već menja stanje

---

### Greška pri kreiranju registry-a

```java
try {
    LocateRegistry.createRegistry(1099);
} catch (RemoteException e) {
    // Registry već postoji – to je OK, nastavljamo dalje
}
```

Ovo je standardni obrazac – ne propagirati ovu grešku.

---

### Struktura Impl konstruktora – uvek ista

```java
protected NazivImpl() throws RemoteException {
    super(); // OBAVEZNO
    // inicijalizacija polja
}
```

`super()` poziva `UnicastRemoteObject` konstruktor koji exportuje objekat za RMI.

---

### Unutrašnja klasa za Callback – uvek isti obrazac

```java
public class NazivCallbackImpl extends UnicastRemoteObject implements NazivCallback {

    protected NazivCallbackImpl() throws RemoteException {
        super();
    }

    @Override
    public void notifikacijaMetoda(...) throws RemoteException {
        // poziva metodu spoljne klase
        spoljaMetoda(...);
    }
}
```

- Uvek `public` unutrašnja klasa (ne `private`, ne `static`)
- Konstruktor `protected` sa `throws RemoteException` i `super()`
- Poziva metode spoljne klase direktno (ima pristup jer nije `static`)