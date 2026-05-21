using Jun24;
using Grpc.Net.Client;

AppContext.SetSwitch("System.Net.Http.SocketsHttpHandler.Http2UnencryptedSupport", true);

using var channel = GrpcChannel.ForAddress("http://localhost:5299");
var client = new MessageService.MessageServiceClient(channel);

async Task IspisiSvePoruke()
{
    Console.WriteLine("  Lista poruka:");
    using var stream = client.ListMessages(new ListMessagesRequest());
    while (await stream.ResponseStream.MoveNext(CancellationToken.None))
        Console.WriteLine($"    [{stream.ResponseStream.Current.Id}] {stream.ResponseStream.Current.Content}");
}

Console.WriteLine("=== gRPC MessageService ===\n");

// Dodavanje poruka
var r1 = await client.SendMessageAsync(new SendMessageRequest { Content = "Prva poruka" });
Console.WriteLine($"Dodata poruka, ID: {r1.Id}");

var r2 = await client.SendMessageAsync(new SendMessageRequest { Content = "Druga poruka" });
Console.WriteLine($"Dodata poruka, ID: {r2.Id}");

var r3 = await client.SendMessageAsync(new SendMessageRequest { Content = "Treca poruka" });
Console.WriteLine($"Dodata poruka, ID: {r3.Id}");

Console.WriteLine();
await IspisiSvePoruke();

// Brisanje poruke
Console.WriteLine($"\nBrisanje poruke sa ID={r2.Id}...");
var del = await client.DeleteMessageAsync(new DeleteMessageRequest { Id = r2.Id });
Console.WriteLine($"  Uspesno: {del.Success}");

Console.WriteLine();
await IspisiSvePoruke();

// Client streaming — šaljemo tok poruka odjednom, server vrati koliko je primio
Console.WriteLine("\n--- Client Streaming: UploadMessages ---");
using (var upload = client.UploadMessages())
{
    var poruke = new[] { "Upload A", "Upload B", "Upload C", "Upload D" };
    foreach (var p in poruke)
    {
        Console.WriteLine($"  Saljem: {p}");
        await upload.RequestStream.WriteAsync(new SendMessageRequest { Content = p });
    }
    await upload.RequestStream.CompleteAsync();
    var uploadOdgovor = await upload.ResponseAsync;
    Console.WriteLine($"  Server primio ukupno: {uploadOdgovor.Sacuvano} poruka");
}

// Bidirectional streaming — šaljemo poruke, server odmah odgovara na svaku
Console.WriteLine("\n--- Bidirectional Streaming: Chat ---");
using var chat = client.Chat();
var slanje = Task.Run(async () =>
{
    var chatPoruke = new[] { "Zdravo", "Kako si", "Cao" };
    foreach (var p in chatPoruke)
    {
        Console.WriteLine($"  [Klijent] Saljem: {p}");
        await chat.RequestStream.WriteAsync(new SendMessageRequest { Content = p });
        await Task.Delay(100);
    }
    await chat.RequestStream.CompleteAsync();
});
while (await chat.ResponseStream.MoveNext(CancellationToken.None))
    Console.WriteLine($"  [Server]  {chat.ResponseStream.Current.Content}");
await slanje;
