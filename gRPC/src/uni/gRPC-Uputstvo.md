# gRPC u .NET — Kompletno uputstvo

> Bazirano na projektima: **Waver**, **Waver2026**, **Studenti/Elfak**

---

## Sadržaj

1. [Šta je gRPC](#1-šta-je-grpc)
2. [Proto fajl — osnova svega](#2-proto-fajl--osnova-svega)
3. [Četiri tipa RPC poziva](#3-četiri-tipa-rpc-poziva)
4. [Serverski deo](#4-serverski-deo)
5. [Klijentski deo](#5-klijentski-deo)
6. [Kompletni primeri po tipu](#6-kompletni-primeri-po-tipu)
7. [Posebne tehnike](#7-posebne-tehnike)
8. [Česte greške i saveti](#8-česte-greške-i-saveti)

---

## 1. Šta je gRPC

gRPC je RPC (Remote Procedure Call) framework koji koristi:
- **Protocol Buffers (protobuf)** — za serijalizaciju poruka (brže i manje od JSON-a)
- **HTTP/2** — za transport (multipleksiranje, streaming, kompresija)

Kontrakt između servera i klijenta definišemo u `.proto` fajlu, a alati automatski generišu C# kod.

---

## 2. Proto fajl — osnova svega

`.proto` fajl definiše **poruke** i **servise**. Isti fajl se kopira i na server i na klijenta.

### Osnovna sintaksa

```proto
syntax = "proto3";

// Namespace koji će biti generisan u C#
option csharp_namespace = "MojProjekt";

// Paket (opciono, koristi se za organizaciju)
package mojpaket;
```

### Definisanje poruka

```proto
message Student {
    int32 brojIndeksa = 1;    // tag broj — mora biti jedinstven u okviru message-a
    string imePrezime = 2;
}

message Indeks {
    int32 brojIndeksa = 1;
}

message IndeksOdDo {
    int32 odBrojaIndeksa = 1;
    int32 doBrojaIndeksa = 2;
}

message Poruka {
    string Text = 1;
}
```

**Ugrađeni tipovi:** `int32`, `int64`, `float`, `double`, `bool`, `string`, `bytes`

### Prazan odgovor — `google.protobuf.Empty`

Kada metoda ne vraća ništa, importujemo `Empty`:

```proto
syntax = "proto3";
import "google/protobuf/empty.proto";

service MojServis {
    rpc ObrisiStavku(Indeks) returns (google.protobuf.Empty);
}
```

### Definisanje servisa — 4 tipa RPC

```proto
service DS26 {
    rpc Unary                 (Number)         returns (Number);          // unary
    rpc ClientStreaming        (stream Number)  returns (Number);          // client streaming
    rpc ServerStreaming        (Number)         returns (stream Number);   // server streaming
    rpc BidirectionalStreaming (stream Number)  returns (stream Number);   // bidi streaming
}
```

Ključna reč `stream` ispred parametra znači da se šalje/prima tok poruka umesto jedne.

---

## 3. Četiri tipa RPC poziva

| Tip | Zahtev | Odgovor | Opis |
|-----|--------|---------|------|
| **Unary** | jedna poruka | jedna poruka | klasičan request-response |
| **Client Streaming** | tok poruka | jedna poruka | klijent šalje više, server vraća jedan rezultat |
| **Server Streaming** | jedna poruka | tok poruka | server vraća više odgovora na jedan zahtev |
| **Bidirectional Streaming** | tok poruka | tok poruka | obe strane šalju paralelno |

---

## 4. Serverski deo

### NuGet paketi (server)

```xml
<PackageReference Include="Grpc.AspNetCore" Version="2.57.0" />
```

> `Grpc.AspNetCore` uključuje sve što treba za server: Grpc.Core, protobuf generatore, ASP.NET integraciju.

### .csproj konfiguracija (server)

```xml
<Project Sdk="Microsoft.NET.Sdk.Web">

  <PropertyGroup>
    <TargetFramework>net8.0</TargetFramework>
    <Nullable>enable</Nullable>
    <ImplicitUsings>enable</ImplicitUsings>
  </PropertyGroup>

  <!-- Svaki proto fajl koji server koristi -->
  <ItemGroup>
    <Protobuf Include="Protos\elfak.proto" GrpcServices="Server" />
    <Protobuf Include="Protos\greet.proto" GrpcServices="Server" />
  </ItemGroup>

  <ItemGroup>
    <PackageReference Include="Grpc.AspNetCore" Version="2.57.0" />
  </ItemGroup>

</Project>
```

**`GrpcServices="Server"`** — generiše se samo serverska bazna klasa (`XyzBase`).

### Program.cs (server)

```csharp
using MojProjekt.Services;

var builder = WebApplication.CreateBuilder(args);

// Registracija gRPC servisa
builder.Services.AddGrpc();

var app = builder.Build();

// Mapiranje svakog servisa na endpoint
app.MapGrpcService<MojServis>();
app.MapGrpcService<DrugiServis>();

// Poruka za browser (gRPC ne radi sa HTTP/1.1)
app.MapGet("/", () => "gRPC endpoint — koristiti gRPC klijenta");

app.Run();
```

### Implementacija servisa

Servis nasleđuje generisanu baznu klasu `ImeServisa.ImeServisaBase`:

```csharp
using Grpc.Core;

public class ElfakService : Elfak.ElfakBase
{
    // Unary
    public override Task<Poruka> DodajStudenta(Student request, ServerCallContext context)
    {
        // request — ulazna poruka
        // context — metapodaci poziva (headers, deadline, cancellation token...)
        return Task.FromResult(new Poruka { Text = "Uspesno" });
    }

    // Client Streaming — klijent šalje stream, server vraća jednu poruku
    public override async Task DodajStudente(
        IAsyncStreamReader<Student> requestStream,
        IServerStreamWriter<Poruka> responseStream,
        ServerCallContext context)
    {
        await foreach (var student in requestStream.ReadAllAsync())
        {
            // obrada svakog studenta
            await responseStream.WriteAsync(new Poruka { Text = $"Dodat: {student.ImePrezime}" });
        }
    }

    // Server Streaming — server šalje stream kao odgovor
    public override async Task PreuzmiStudente(
        IndeksOdDo request,
        IServerStreamWriter<Student> responseStream,
        ServerCallContext context)
    {
        var studenti = Baza.Where(s => s.Key >= request.OdBrojaIndeksa && s.Key <= request.DoBrojaIndeksa);
        foreach (var sv in studenti)
        {
            await responseStream.WriteAsync(new Student
            {
                BrojIndeksa = sv.Key,
                ImePrezime = sv.Value
            });
        }
    }

    // Bidirectional Streaming
    public override async Task BidirectionalStreaming(
        IAsyncStreamReader<Student> requestStream,
        IServerStreamWriter<Poruka> responseStream,
        ServerCallContext context)
    {
        await foreach (var student in requestStream.ReadAllAsync())
        {
            await responseStream.WriteAsync(new Poruka { Text = $"Primljeno: {student.ImePrezime}" });
        }
    }
}
```

### Potpisi metoda po tipu (server)

```csharp
// Unary
Task<TResponse> MetodaNaziv(TRequest request, ServerCallContext context)

// Client Streaming
Task<TResponse> MetodaNaziv(IAsyncStreamReader<TRequest> requestStream, ServerCallContext context)

// Server Streaming
Task MetodaNaziv(TRequest request, IServerStreamWriter<TResponse> responseStream, ServerCallContext context)

// Bidirectional Streaming
Task MetodaNaziv(IAsyncStreamReader<TRequest> requestStream, IServerStreamWriter<TResponse> responseStream, ServerCallContext context)
```

---

## 5. Klijentski deo

### NuGet paketi (klijent — Console app)

```xml
<PackageReference Include="Google.Protobuf" Version="3.25.3" />
<PackageReference Include="Grpc.Net.Client" Version="2.61.0" />
<PackageReference Include="Grpc.Tools" Version="2.62.0">
    <PrivateAssets>all</PrivateAssets>
    <IncludeAssets>runtime; build; native; contentfiles; analyzers; buildtransitive</IncludeAssets>
</PackageReference>
```

> `Grpc.Tools` je build-time alat, ne isporučuje se uz aplikaciju (`PrivateAssets>all`).

### .csproj konfiguracija (klijent)

```xml
<Project Sdk="Microsoft.NET.Sdk">

  <PropertyGroup>
    <OutputType>Exe</OutputType>
    <TargetFramework>net8.0</TargetFramework>
    <ImplicitUsings>enable</ImplicitUsings>
    <Nullable>enable</Nullable>
  </PropertyGroup>

  <ItemGroup>
    <!-- Isti proto fajl kao na serveru -->
    <Protobuf Include="Protos\elfak.proto" GrpcServices="Client" />
  </ItemGroup>

  <ItemGroup>
    <PackageReference Include="Google.Protobuf" Version="3.25.3" />
    <PackageReference Include="Grpc.Net.Client" Version="2.61.0" />
    <PackageReference Include="Grpc.Tools" Version="2.62.0">
      <PrivateAssets>all</PrivateAssets>
      <IncludeAssets>runtime; build; native; contentfiles; analyzers; buildtransitive</IncludeAssets>
    </PackageReference>
  </ItemGroup>

</Project>
```

**`GrpcServices="Client"`** — generiše se samo klijentska klasa (`XyzClient`).

### Kreiranje kanala i klijenta

```csharp
using Grpc.Net.Client;

// Kanal = konekcija ka serveru (reusable, thread-safe)
using var channel = GrpcChannel.ForAddress("http://localhost:5000");

// Klijent se pravi nad kanalom
var client = new Elfak.ElfakClient(channel);
```

> `using var` — kanal se automatski zatvori na kraju bloka.

---

## 6. Kompletni primeri po tipu

### Unary

```csharp
// Server
public override Task<Poruka> DodajStudenta(Student request, ServerCallContext context)
{
    Baza.Add(request.BrojIndeksa, request.ImePrezime);
    return Task.FromResult(new Poruka { Text = $"Dodat student {request.BrojIndeksa}" });
}

// Klijent
var odgovor = await client.DodajStudentaAsync(new Student
{
    BrojIndeksa = 12345,
    ImePrezime = "Pera Peric"
});
Console.WriteLine(odgovor.Text);
```

### Client Streaming

Klijent šalje više poruka, server prima sve i vraća jedan rezultat.

```csharp
// Server
public override async Task<Number> ClientStreaming(
    IAsyncStreamReader<Number> requestStream,
    ServerCallContext context)
{
    int sum = 0;
    await foreach (var number in requestStream.ReadAllAsync())
        sum += number.Value;
    return new Number { Value = sum };
}

// Klijent
var call = client.ClientStreaming();

for (int i = 1; i <= 10; i++)
    await call.RequestStream.WriteAsync(new Number { Value = i });

await call.RequestStream.CompleteAsync(); // obavezno — signalizira kraju streama
var rezultat = await call;               // čeka odgovor servera
Console.WriteLine(rezultat.Value);       // 55
```

### Server Streaming

Klijent šalje jednu poruku, server vraća tok odgovora.

```csharp
// Server
public override async Task ServerStreaming(
    Number request,
    IServerStreamWriter<Number> responseStream,
    ServerCallContext context)
{
    for (int i = 1; i <= request.Value; i++)
        await responseStream.WriteAsync(new Number { Value = i });
}

// Klijent
var call = client.ServerStreaming(new Number { Value = 10 });

await foreach (var odgovor in call.ResponseStream.ReadAllAsync())
    Console.Write(odgovor.Value + " "); // 1 2 3 4 5 6 7 8 9 10
Console.WriteLine();
```

### Bidirectional Streaming

Obe strane šalju i primaju paralelno.

```csharp
// Server
public override async Task BidirectionalStreaming(
    IAsyncStreamReader<Number> requestStream,
    IServerStreamWriter<Number> responseStream,
    ServerCallContext context)
{
    await foreach (var number in requestStream.ReadAllAsync())
        await responseStream.WriteAsync(new Number { Value = number.Value + 1 });
}

// Klijent
var call = client.BidirectionalStreaming();

// Čitanje odgovora u pozadinskom tasku
var pozadinskiTask = Task.Run(async () =>
{
    await foreach (var odgovor in call.ResponseStream.ReadAllAsync())
        Console.WriteLine($"Server odgovorio: {odgovor.Value}");
});

// Slanje poruka
for (int i = 11; i <= 20; i++)
{
    await call.RequestStream.WriteAsync(new Number { Value = i });
    Thread.Sleep(1000); // simulacija sporih poruka
}

await call.RequestStream.CompleteAsync(); // zatvori stream
await pozadinskiTask;                     // sačekaj da se prime svi odgovori
```

---

## 7. Posebne tehnike

### Singleton za deljene podatke (thread-safe)

Koristi se kada više gRPC poziva treba da deli iste podatke u memoriji:

```csharp
public class Studenti
{
    public Dictionary<int, string> Baza { get; set; }

    private static Studenti instanca;
    private static object lockObj = new object();

    private Studenti()
    {
        Baza = new Dictionary<int, string>();
    }

    public static Studenti Instanca()
    {
        if (instanca == null)
        {
            lock (lockObj)
            {
                if (instanca == null)
                    instanca = new Studenti();
            }
        }
        return instanca;
    }
}

// Korišćenje u servisu
Studenti.Instanca().Baza.Add(request.BrojIndeksa, request.ImePrezime);
```

> Double-checked locking — bezbedno za višenitno okruženje.

### Vraćanje `google.protobuf.Empty`

```csharp
// Proto
import "google/protobuf/empty.proto";
rpc ObrisiStudenta(Indeks) returns (google.protobuf.Empty);

// C# using
using Google.Protobuf.WellKnownTypes;

// Server implementacija
public override Task<Empty> ObrisiStudenta(Indeks request, ServerCallContext context)
{
    Baza.Remove(request.BrojIndeksa);
    return Task.FromResult(new Empty());
}

// Klijent
await client.ObrisiStudentaAsync(new Indeks { BrojIndeksa = 12345 });
// odgovor se ignoriše
```

### Više servisa na jednom serveru

```csharp
// Program.cs
builder.Services.AddGrpc();

app.MapGrpcService<GreeterService>();
app.MapGrpcService<ElfakService>();
app.MapGrpcService<DS26Service>();
```

Svaki servis mora biti posebno registrovan sa `MapGrpcService<T>`.

### Rukovanje greškama na klijentu

```csharp
try
{
    var odgovor = await client.DodajStudentaAsync(new Student { ... });
}
catch (RpcException ex)
{
    Console.WriteLine($"gRPC greška: {ex.StatusCode} — {ex.Message}");
}
catch (Exception e)
{
    Console.WriteLine(e);
}
```

---

## 8. Česte greške i saveti

### Proto fajl mora biti isti na serveru i klijentu

Kopirati isti `.proto` fajl u `Protos/` folder i na serveru i na klijentu. Razlika je samo u `.csproj`:
- Server: `GrpcServices="Server"`
- Klijent: `GrpcServices="Client"`

### `CompleteAsync()` — obavezno kod client/bidi streaminga

```csharp
await call.RequestStream.CompleteAsync(); // server zna da nema više poruka
var rezultat = await call;               // tek posle ovoga čekamo odgovor
```

Bez `CompleteAsync()` server nikad neće izaći iz `ReadAllAsync()` petlje.

### Pozadinski task za bidi streaming na klijentu

Čitanje i pisanje moraju biti paralelni — ne sme se blokovati:

```csharp
var pozadinskiTask = Task.Run(async () =>
{
    await foreach (var odgovor in call.ResponseStream.ReadAllAsync())
        Console.WriteLine(odgovor.Value);
});

// Slanje...
await call.RequestStream.CompleteAsync();
await pozadinskiTask; // čekamo da se završi čitanje
```

### Port konfiguracija

Podrazumevani port je `5000` (HTTP). Može se promeniti u `launchSettings.json` ili `appsettings.json`:

```json
// launchSettings.json
"applicationUrl": "http://localhost:5110"
```

Klijent se tada konektuje na isti port:

```csharp
using var channel = GrpcChannel.ForAddress("http://localhost:5110");
```

### Generisane klase

Na osnovu `.proto` fajla se generišu:
- `ImeServisa.ImeServisaBase` — bazna klasa za server implementaciju
- `ImeServisa.ImeServisaClient` — klijentska klasa za pozivanje
- Klase za sve poruke (`Student`, `Indeks`, itd.)

---

## Struktura foldera

```
MojGrpcProjekat/
├── Server/
│   ├── Server.csproj          (GrpcServices="Server", Grpc.AspNetCore)
│   ├── Program.cs             (AddGrpc, MapGrpcService)
│   ├── Protos/
│   │   └── mojservis.proto
│   └── Services/
│       └── MojServis.cs       (nasleđuje MojServis.MojServisBase)
│
└── Client/
    ├── Client.csproj          (GrpcServices="Client", Grpc.Net.Client, Google.Protobuf, Grpc.Tools)
    ├── Program.cs             (GrpcChannel.ForAddress, new XyzClient(channel))
    └── Protos/
        └── mojservis.proto    (isti fajl kao na serveru)
```

---

## Brza referenca — tipovi potpisa

### Server

| Tip | Potpis |
|-----|--------|
| Unary | `Task<TRes> Naziv(TReq request, ServerCallContext ctx)` |
| Client Streaming | `Task<TRes> Naziv(IAsyncStreamReader<TReq> reqStream, ServerCallContext ctx)` |
| Server Streaming | `Task Naziv(TReq request, IServerStreamWriter<TRes> resStream, ServerCallContext ctx)` |
| Bidi Streaming | `Task Naziv(IAsyncStreamReader<TReq> reqStream, IServerStreamWriter<TRes> resStream, ServerCallContext ctx)` |

### Klijent

| Tip | Poziv |
|-----|-------|
| Unary | `await client.NazivAsync(request)` |
| Client Streaming | `var call = client.Naziv(); await call.RequestStream.WriteAsync(...); await call.RequestStream.CompleteAsync(); var res = await call;` |
| Server Streaming | `var call = client.Naziv(request); await foreach (var x in call.ResponseStream.ReadAllAsync()) { }` |
| Bidi Streaming | `var call = client.Naziv(); Task.Run(() => foreach odgovor...); for slanje...; CompleteAsync(); await task;` |
