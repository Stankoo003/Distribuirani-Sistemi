# gRPC

Projekti realizovani korišćenjem **gRPC (Google Remote Procedure Call)** tehnologije.

## Šta je gRPC?

gRPC je moderni, visoko-performantni RPC framework koji koristi **Protocol Buffers** kao jezik za definisanje interfejsa i format poruka. Podržava bidirekcione streaming pozive i radi nad HTTP/2.

## Struktura

```
gRPC/
├── README.md
├── proto/          ← .proto definicije servisa
└── src/            ← izvorni kod (server + klijent)
```

## Pokretanje

1. Generisati kod iz `.proto` fajlova
2. Pokrenuti gRPC server
3. Pokrenuti klijenta

## Zadaci

Specifikacije zadataka se nalaze u [`../docs/`](../docs/).
