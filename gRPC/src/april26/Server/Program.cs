using April26.Services;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddGrpc();

var app = builder.Build();

app.MapGrpcService<KalkulatorService>();
app.MapGet("/", () => "gRPC Kalkulator server — koristiti gRPC klijenta.");

app.Run();
