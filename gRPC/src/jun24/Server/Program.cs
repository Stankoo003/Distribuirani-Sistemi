using Jun24.Services;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddGrpc();

var app = builder.Build();

app.MapGrpcService<MessageService>();
app.MapGet("/", () => "gRPC MessageService server — koristiti gRPC klijenta.");

app.Run();
